import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BouncyCastle {
    private final X509Certificate certificate;
    private final PrivateKey privateKey;

    BouncyCastle() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
        certificate = (X509Certificate) certFactory.generateCertificate(new FileInputStream("public.cer"));
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream("private.p12"), "password".toCharArray());
        privateKey = (PrivateKey) keystore.getKey("baeldung", "password".toCharArray());
    }

    public byte[] encryptBytes(byte[] data) throws Exception {
        return encryptData(data, certificate);
    }

    public byte[] decryptBytes(byte[] data) throws Exception {
        return decryptData(data, privateKey);
    }

    public byte[] signBytes(byte[] data) throws Exception {
        return signData(data, certificate, privateKey);
    }

    public boolean verifySignedBytes(byte[] data) {
        try {
            return verifySignedData(data);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifySignedData(byte[] signedData) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedData);
        ASN1InputStream asnInputStream = new ASN1InputStream(inputStream);
        CMSSignedData cmsSignedData = new CMSSignedData(ContentInfo.getInstance(asnInputStream.readObject()));
        SignerInformationStore signers = cmsSignedData.getSignerInfos();
        SignerInformation signer = signers.getSigners().iterator().next();
        Collection<X509CertificateHolder> certCollection = cmsSignedData.getCertificates().getMatches(signer.getSID());
        X509CertificateHolder certHolder = certCollection.iterator().next();
        return signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
                .build(certHolder));
    }

    private byte[] signData(byte[] data, X509Certificate signingCertificate, PrivateKey signingKey) throws Exception {
        if (data == null || signingCertificate == null) throw new IllegalArgumentException();
        List<X509Certificate> certList = new ArrayList<>();
        CMSTypedData cmsData = new CMSProcessableByteArray(data);
        certList.add(signingCertificate);
        Store certs = new JcaCertStore(certList);
        CMSSignedDataGenerator cmsGenerator = new CMSSignedDataGenerator();
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(signingKey);
        cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().setProvider("BC")
                        .build()).build(contentSigner, signingCertificate));
        cmsGenerator.addCertificates(certs);
        CMSSignedData cms = cmsGenerator.generate(cmsData, true);
        return cms.getEncoded();
    }

    private byte[] encryptData(byte[] data, X509Certificate encryptionCertificate) throws Exception {
        if (data == null || encryptionCertificate == null) throw new IllegalArgumentException();
        CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
        JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
        cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
        CMSTypedData msg = new CMSProcessableByteArray(data);
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                .setProvider("BC").build();
        CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
        return cmsEnvelopedData.getEncoded();
    }

    private byte[] decryptData(byte[] encryptedData, PrivateKey decryptionKey) throws CMSException {
        if (null == encryptedData || null == decryptionKey) throw new IllegalArgumentException();
        CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);
        Collection<RecipientInformation> recipients = envelopedData.getRecipientInfos().getRecipients();
        KeyTransRecipientInformation recipientInfo = (KeyTransRecipientInformation) recipients.iterator().next();
        JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(decryptionKey);
        return recipientInfo.getContent(recipient);
    }
}
