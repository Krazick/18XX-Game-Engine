package ge18xx.company;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Mockito;

public class CertificateTestFactory {

	public CertificateTestFactory () {
	}

	public Certificate buildCertificateMock () {
		Certificate mCertificate;

		mCertificate = Mockito.mock (Certificate.class);
		
		return mCertificate;
	}

	public List<Certificate> buildListCertificatesMock () {
		List<Certificate> mCertificatesList;
		Certificate mCertificate = Mockito.mock (Certificate.class);

		mCertificatesList = new LinkedList<Certificate> ();
		mCertificate = buildCertificateMock ();
		mCertificatesList.add (mCertificate);
		mCertificate = buildCertificateMock ();
		mCertificatesList.add (mCertificate);
		
		return mCertificatesList;
	}

	public LoadedCertificate buildLoadedCertificateMock () {
		LoadedCertificate mLoadedCertificate;
		
		mLoadedCertificate = Mockito.mock (LoadedCertificate.class);
		
		return mLoadedCertificate;
	}
}
