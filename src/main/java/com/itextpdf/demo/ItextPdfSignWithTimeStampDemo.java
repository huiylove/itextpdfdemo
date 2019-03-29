package com.itextpdf.demo;

import com.itextpdf.signature.SignatureInfo;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.util.ProperUtils;
import com.itextpdf.util.SignPdfUtil;

public class ItextPdfSignWithTimeStampDemo {
	
	public static final String KEYSTORE_1 = ProperUtils.getInitParam("keystore_1");//证书地址

    public static final String PASSWORD = ProperUtils.getInitParam("password");//"cfca1234";//密钥密码

    public static final String SIGNIMAGE_1 = ProperUtils.getInitParam("signimage_1");
    

    public static final String SIGN_PDF_SRC = ProperUtils.getInitParam("signpdf_src");//需签名的pdf文件路径
    public static final String SIGN_PDF_DEST = ProperUtils.getInitParam("signpdf_dest"); //签完名的pdf文件路径

    public static final String SIGN_TSA_URL = ProperUtils.getInitParam("sign_tsa_url");//时间戳服务地址  "http://tsa.safelayer.com:8093";

    public static final String SIGN_TSA_ACCNT = ProperUtils.getInitParam("sign_tsa_accnt");
    public static final String SIGN_TSA_PASSW = ProperUtils.getInitParam("sign_tsa_passw");
    
	public static void main(String[] args) {
		try {
			SignPdfUtil signPdfUtil = new SignPdfUtil(SIGN_TSA_URL,SIGN_TSA_ACCNT,SIGN_TSA_PASSW);
			//封装签章信息
            SignatureInfo info1 = new SignatureInfo.Builder(KEYSTORE_1,PASSWORD).reason("").location("").certificationLevel(PdfSignatureAppearance.NOT_CERTIFIED)
            		.digestAlgorithm(DigestAlgorithms.SHA1).renderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC).fieldName("sign1").pk().chain().subfilter(CryptoStandard.CADES).chain().imagePath(SIGNIMAGE_1).x(150).y(200).page(1).build();
//            SignatureInfo info2 = new SignatureInfo.Builder(KEYSTORE_2, PASSWORD).reason("").location("").certificationLevel(PdfSignatureAppearance.NOT_CERTIFIED)
//            		.digestAlgorithm(DigestAlgorithms.SHA1).renderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC).fieldName("sign2").pk().chain().subfilter(CryptoStandard.CADES).chain().imagePath(SIGNIMAGE_2).x(150).y(600).page(1).build();
			signPdfUtil.sign(SIGN_PDF_SRC,SIGN_PDF_DEST,info1);
			System.out.println("pdf数字签章===签名完成===");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
