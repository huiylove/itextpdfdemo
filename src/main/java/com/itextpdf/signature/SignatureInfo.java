package com.itextpdf.signature;

import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.MakeSignature;

public class SignatureInfo {
	
	private SignerKeystore signerKeystore;
	private String reason; //理由
    private String location;//位置
    private String digestAlgorithm;//摘要类型
    private String imagePath;//图章路径
    private float x;//图章对应x轴和y轴坐标
    private float y;
    private int page;//图章在PDF文档的页数
	private String fieldName;//表单域名称
    private Certificate[] chain;//证书链
    private PrivateKey pk;//私钥
    private int certificationLevel = 0; //批准签章
    private PdfSignatureAppearance.RenderingMode renderingMode;//表现形式：仅描述，仅图片，图片和描述，签章者和描述
    private MakeSignature.CryptoStandard subfilter;//支持标准，CMS,CADES
    
    public SignatureInfo(){
    	
    }
    
    public SignatureInfo(SignatureInfo target){
    	this.reason = target.reason;
    	this.location = target.location;
    	this.digestAlgorithm = target.digestAlgorithm;
    	this.imagePath = target.imagePath;
    	this.x = target.x;
    	this.y = target.y;
    	this.page = target.page;
    	this.fieldName = target.fieldName;
    	this.chain = target.chain;
    	this.pk = target.pk;
    	this.certificationLevel = target.certificationLevel;
    	this.renderingMode = target.renderingMode;
    	this.subfilter = target.subfilter;
    }
    
    /**
     * Student的创建完全依靠Student.Builder，使用一种方法链的方式来创建
     *
     */
    public static class Builder {

        private SignatureInfo target;

        public Builder(String cert_path,String cert_passw) {
        	target = new SignatureInfo();
            try {
				target.signerKeystore = new SignerKeystorePKCS12(new FileInputStream(cert_path),cert_passw);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        public Builder reason(String reason) {
            target.reason = reason;
            return this;
        }

        public Builder location(String location) {
            target.location = location;
            return this;
        }

        public Builder digestAlgorithm(String digestAlgorithm) {
            target.digestAlgorithm = digestAlgorithm;
            return this;
        }

        public Builder imagePath(String imagePath) {
            target.imagePath = imagePath;
            return this;
        }

        public Builder x(float x) {
            target.x = x;
            return this;
        }
        
        public Builder y(float y) {
            target.y = y;
            return this;
        }
        
        public Builder fieldName(String fieldName) {
            target.fieldName = fieldName;
            return this;
        }
        
        public Builder page(int page) {
            target.page = page;
            return this;
        }
        
        public Builder chain() {
            target.chain = target.signerKeystore.getChain();
            return this;
        }
        
        public Builder pk() {
            target.pk = target.signerKeystore.getPrivateKey();
            return this;
        }
        
        public Builder certificationLevel(int certificationLevel) {
        	target.certificationLevel = certificationLevel;
        	return this;
        }
        
        public Builder renderingMode(PdfSignatureAppearance.RenderingMode renderingMode) {
        	target.renderingMode = renderingMode;
        	return this;
        }
        
        public Builder subfilter(MakeSignature.CryptoStandard subfilter) {
        	target.subfilter = subfilter;
        	return this;
        }

        public SignatureInfo build() {
            return new SignatureInfo(target);
        }
        
    }

	public SignerKeystore getSignerKeystore() {
		return signerKeystore;
	}

	public String getReason() {
		return reason;
	}

	public String getLocation() {
		return location;
	}

	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public String getImagePath() {
		return imagePath;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getPage() {
		return page;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Certificate[] getChain() {
		return chain;
	}

	public PrivateKey getPk() {
		return pk;
	}

	public int getCertificationLevel() {
		return certificationLevel;
	}

	public PdfSignatureAppearance.RenderingMode getRenderingMode() {
		return renderingMode;
	}

	public MakeSignature.CryptoStandard getSubfilter() {
		return subfilter;
	}
    
    
}
