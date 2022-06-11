package ge18xx.company;

import org.mockito.Mockito;

public class CertificateTestFactory {

	public CertificateTestFactory () {
		// TODO Auto-generated constructor stub
	}

	public Certificate buildCertificateMock (String aCompanyAbbrev) {
		Certificate mCertificate = Mockito.mock (Certificate.class);

		Mockito.when (mCertificate.getCompanyAbbrev ()).thenReturn (aCompanyAbbrev);

		return mCertificate;
	}

}
