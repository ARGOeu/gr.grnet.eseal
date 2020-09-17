# gr.grnet.eseal
E-signature library

### PDF Validation using an X509 certificate

```java
import gr.grnet.eseal.PDFValidator;
import gr.grnet.eseal.ValidationLevel;
import gr.grnet.eseal.ValidationReport;
import gr.grnet.eseal.X509CertificateTrustSource;

public class Example 
{
    public static void main( String[] args ) {
        
        // Initialise the pdf validator from a file source
        PDFValidator pdf = new PDFValidator("/path/to/pdf/file");
        
        
        try {
            
            // Initialise the x509 trust source from a file source
            X509CertificateTrustSource x509CertificateTrustSource = new X509CertificateTrustSource("/path/to/cert");
            
            // Validate the document based on the provided trust source(x509 cert) and the validation severity
            ValidationReport r =  pdf.validate(ValidationLevel.BASIC_SIGNATURES, x509CertificateTrustSource);
            
            // get the result of the validation process
            System.out.println(r.getValidationResult());
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

### PDF Validation using a Java trustore

```java
import gr.grnet.eseal.KeyStoreType;
import gr.grnet.eseal.KeystoreTrustSource;
import gr.grnet.eseal.PDFValidator;
import gr.grnet.eseal.ValidationReport;
import gr.grnet.eseal.ValidationLevel;

public class Example2
{
    public static void main( String[] args ) {

        // Initialise the pdf validator from a file source
        PDFValidator pdf = new PDFValidator("/path/to/pdf");


        try {

            String keystorePath = "/path/to/trustore";
            String password = "eseal12345";
            
            // Initialise the trustore trust source from a file source
            KeystoreTrustSource keystoreTrustSource = new KeystoreTrustSource(keystorePath, password, KeyStoreType.JKS);

            // Validate the document based on the provided trust source(trustore) and the validation severity
            ValidationReport r =  pdf.validate(ValidationLevel.BASIC_SIGNATURES, keystoreTrustSource);

            // get the result of the validation process
            System.out.println(r.getValidationResult());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

```

### PDF Validation using a Trusted List

```java
import gr.grnet.eseal.PDFValidator;
import gr.grnet.eseal.TLTrustSource;
import gr.grnet.eseal.ValidationLevel;
import gr.grnet.eseal.ValidationReport;
import gr.grnet.eseal.TrustedListURL;

public class Example3 {

    public static void main( String[] args ) {

        // Initialise the pdf validator from a file source
        PDFValidator pdf = new PDFValidator("/path/to/pdf");


        try {
            
             // Initialise the trusted list source with the greek trusted list( https://www.eett.gr/tsl/EL-TSL.xml)
            TLTrustSource tlTrustSource = new TLTrustSource(TrustedListURL.GREECE);

            // Validate the document based on the provided trust source(trusted list) and the validation severity
            ValidationReport r =  pdf.validate(ValidationLevel.BASIC_SIGNATURES, tlTrustSource);

            // get the result of the validation process
            System.out.println(r.getValidationResult());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

### PDF Validation using a List of Trusted Lists

```java
import gr.grnet.eseal.PDFValidator;
import gr.grnet.eseal.LOTLTrustSource;
import gr.grnet.eseal.ValidationLevel;
import gr.grnet.eseal.ValidationReport;
import gr.grnet.eseal.LOTLURL;
public class Example4 {

    public static void main( String[] args ) {

        // Initialise the pdf validator from a file source
        PDFValidator pdf = new PDFValidator("/path/to/pdf");


        try {
            
            // Initialise the list of trusted list source from the european lotl
            LOTLTrustSource lotlTrustSource = new LOTLTrustSource(LOTLURL.EUROPE);

            // Validate the document based on the provided trust source(list of trusted lists) and the validation severity
            ValidationReport r =  pdf.validate(ValidationLevel.BASIC_SIGNATURES, lotlTrustSource);

            // get the result of the validation process
            System.out.println(r.getValidationResult());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

