package com.itextpdf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertParser;

import com.itextpdf.signature.SignatureInfo;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;



/** 
 * 
 * @author : yuanhui 
 * @date   : 2018年6月26日
 * @version : 1.0
 */
public class SignPdfUtil {

    private TSAClient tsaClient;
    
//    static{
//        BouncyCastleProvider bcp = new BouncyCastleProvider();
//	    //Security.addProvider(bcp);
//	    Security.insertProviderAt(bcp, 1);
//    }
//     
    public SignPdfUtil()  {
       
    }
    
    /**
    *
    * @param tsa_url   tsa服务器地址
    * @param tsa_accnt tsa账户号
    * @param tsa_passw tsa密码
    */
   public SignPdfUtil(String tsa_url,String tsa_accnt,String tsa_passw)  {
     tsaClient = new TSAClientBouncyCastle(tsa_url, tsa_accnt, tsa_passw);
   }
   
   public void sign(String signPdfSrc,String signPdfDest,SignatureInfo... signatureInfos) {
	    InputStream inputStream = null;
        FileOutputStream outputStream = null;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
	    try {
            inputStream = new FileInputStream(signPdfSrc);
	        for (SignatureInfo signatureInfo : signatureInfos) {
	        	PdfReader reader = new PdfReader(inputStream);
	        	ByteArrayOutputStream tempArrayOutputStream = new ByteArrayOutputStream();
                //创建签章工具PdfStamper ，最后一个boolean参数是否允许被追加签名
                PdfStamper stamper = PdfStamper.createSignature(reader,tempArrayOutputStream, '\0', null, true);
                stamper.setFullCompression();
                // 获取数字签章属性对象
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance.setReason(signatureInfo.getReason());
                appearance.setLocation(signatureInfo.getLocation());
                //设置签名的签名域名称，多次追加签名的时候，签名预名称不能一样，图片大小受表单域大小影响（过小导致压缩）signatureInfo
                //读取图章图片
                Image image = Image.getInstance(signatureInfo.getImagePath());
                appearance.setSignatureGraphic(image);
                appearance.setVisibleSignature(new Rectangle(signatureInfo.getX(),signatureInfo.getY(), signatureInfo.getX()+ 120, signatureInfo.getY()+ 120),signatureInfo.getPage(),signatureInfo.getFieldName());
                appearance.setCertificationLevel(signatureInfo.getCertificationLevel());
                //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
                appearance.setRenderingMode(signatureInfo.getRenderingMode());
                stamper.getWriter().setCompressionLevel(5);
                // 摘要算法
                ExternalDigest digest = new BouncyCastleDigest();
                // 签名算法
                ExternalSignature signature = new PrivateKeySignature(signatureInfo.getPk(), signatureInfo.getDigestAlgorithm(),"BC");
                // 调用itext签名方法完成pdf签章
                MakeSignature.signDetached(appearance,digest,signature,signatureInfo.getChain(), null, null,this.tsaClient, 0, signatureInfo.getSubfilter());
                //定义输入流为生成的输出流内容，以完成多次签章的过程
                inputStream = new ByteArrayInputStream(tempArrayOutputStream.toByteArray());
                result = tempArrayOutputStream;
            }
            outputStream = new FileOutputStream(new File(signPdfDest));
            outputStream.write(result.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=outputStream){
                    outputStream.close();
                }
                if(null!=inputStream){
                    inputStream.close();
                }
                if(null!=result){
                    result.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
   
   public static void setUp(){
	     BouncyCastleProvider bcp = new BouncyCastleProvider();
	     //Security.addProvider(bcp);
	     Security.insertProviderAt(bcp, 1);
   }
   
   public static void verifySignature(String signPdfSrc){
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   System.out.println("==============验签开始==============");
//     setUp();
       PdfReader reader;
		try {
			reader = new PdfReader(signPdfSrc);
			AcroFields acroFields = reader.getAcroFields();
			List<String> names = acroFields.getSignatureNames();
            for (String name : names) {
               System.out.println("Signature name: " + name);
               System.out.println("Signature covers whole document: " + acroFields.signatureCoversWholeDocument(name));
               PdfPKCS7 pk = acroFields.verifySignature(name);
               System.out.println("Subject: " + CertificateInfo.getSubjectFields(pk.getSigningCertificate()));
               System.out.println("Date: " + sdf.format(pk.getSignDate().getTime()));
               System.out.println("Document verifies: " + pk.verify());
               System.out.println();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
   
   public static Date getSignDate(String signPdfSrc){
        PdfReader reader;
		try {
			reader = new PdfReader(signPdfSrc);
			AcroFields acroFields = reader.getAcroFields();
			List<String> names = acroFields.getSignatureNames();
            PdfPKCS7 pk = acroFields.verifySignature(names.get(0));
            return pk.getSignDate().getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
   }
   
   
   public static Map<String,HashMap<String,Object>> getSignatureInfo(String signPdfSrc) {
	   Map<String,HashMap<String,Object>> result = null;
	    try {
	        	PdfReader reader = new PdfReader(signPdfSrc);
	        	AcroFields acroFields = reader.getAcroFields();  
	        	if (acroFields == null) {  
                  return result;  
	        	}  
	        	result = new HashMap<String,HashMap<String,Object>>();
	        	//签章名称  
	        	List<String> signatureNames = acroFields.getSignatureNames();  
	        	if (signatureNames == null || signatureNames.size() == 0) {  
	        		return result;  
	        	} 
	            reader.close();  
	            for(String name:signatureNames){
	            	HashMap<String,Object> obj = new HashMap<String,Object>();
	            	//签章对应的字典  
	                PdfDictionary sigDict = acroFields.getSignatureDictionary(name);  
	                if (sigDict == null) {  
	                    continue;  
	                }  
	                PdfName sub = sigDict.getAsName(PdfName.SUBFILTER);  
	                if (PdfName.ADBE_PKCS7_DETACHED.equals(sub)) {  
	                	//签章对应的证书  
	                    PdfString certStr = sigDict.getAsString(PdfName.SIG);  
	                    if (certStr == null) {  
	                        certStr = sigDict.getAsArray(PdfName.SIG)==null?null:sigDict.getAsArray(PdfName.SIG).getAsString(0);  
	                    }  
	                    if (certStr == null) {  
	                        continue;  
	                    }
	                    //签章对应的证书  
	                    X509CertParser certParser = new X509CertParser();  
	                    certParser.engineInit(new ByteArrayInputStream(certStr.getBytes()));  
	                    Collection<Certificate> certs = certParser.engineReadAll();  
	                    if (certs == null || certs.size() == 0) {  
	                        continue;  
	                    }  
	                    X509Certificate certificate = (X509Certificate) certs.iterator().next();  
	                    if (certificate == null) {  
	                        continue;  
	                    }  
	                    X500Principal principal = certificate.getSubjectX500Principal();  
	                    if (principal == null) {  
	                        continue;  
	                    }  
	                    //签章对应的证书的所有者  
	                    LdapName ldapDN = new LdapName(principal.getName());  
	                    for (Rdn rdn : ldapDN.getRdns()) {  
	                        if ("CN".equals(rdn.getType())) {  
	                        	obj.put("ldapname",(String) rdn.getValue());  
	                        }  
	                    }
	                }
	                result.put(name,obj);
	            }
       } catch (Exception e) {
           e.printStackTrace();
       }
	   return result;
   }
   
   public static void main(String[] args) {
	   String signPdfSrc = "C:\\Users\\Administrator\\Desktop\\lceFile.pdf";
//	   Map<String,HashMap<String,Object>> result = SignPdfUtil.getSignatureInfo(signPdfSrc);
	   SignPdfUtil.verifySignature(signPdfSrc);
   }
}
