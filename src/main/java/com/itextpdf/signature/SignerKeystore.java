package com.itextpdf.signature;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;

public interface SignerKeystore {
	
	    public PrivateKey getPrivateKey() ;

	    public Certificate[] getChain() ;

	    public Provider getProvider();
}
