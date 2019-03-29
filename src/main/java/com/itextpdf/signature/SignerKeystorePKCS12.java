package com.itextpdf.signature;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;

public class SignerKeystorePKCS12 implements SignerKeystore {
	
	
	private static Provider prov = null;

    private KeyStore ks;

    private String alias;

    private String pwd;

    private PrivateKey key;

    private Certificate[] chain;

    public SignerKeystorePKCS12(InputStream inp, String passw) throws Exception {

        // This should be done once only for the provider...

        if (prov == null) {

            prov = new org.bouncycastle.jce.provider.BouncyCastleProvider();

            Security.addProvider(prov);

        }

        this.ks = KeyStore.getInstance("pkcs12", prov);

        this.pwd = passw;

        this.ks.load(inp, pwd.toCharArray());

        this.alias = (String)ks.aliases().nextElement();

        this.key   = (PrivateKey)ks.getKey(alias, pwd.toCharArray());

        this.chain = ks.getCertificateChain(alias);

    }


	@Override
	public PrivateKey getPrivateKey() {
		return key;
	}

	@Override
	public Certificate[] getChain() {
		return chain;
	}

	@Override
	public Provider getProvider() {
		return this.prov;
	}

}
