package ge18xx.company;

import org.mockito.Mockito;

public class CertificateTestFactory {

	public CertificateTestFactory () {
	}

	public Certificate buildCertificateMock () {
		Certificate mCertificate = Mockito.mock (Certificate.class);

		return mCertificate;
	}

}
