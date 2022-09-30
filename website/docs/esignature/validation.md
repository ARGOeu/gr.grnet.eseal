---
id: validation
title: PDF Validation API Calls
sidebar_position: 5
keywords:
  - eseal
  - e-seal
  - validation
  - api
  - doc
  - docs
  - documentation
  - documents
  - pdf
  - grnet
---


## POST - Validate a PDF Document

### Request
<b>POST</b> @ <i> /api/v1/validation/validateDocument </i>

### Request Body

- `bytes` : The PDF to be validated should be encoded in base64 format
- `name`: Optional name for the pdf document

```json
{
  "signedDocument" : {
    "bytes" : "JVBERi... ",
    "name": "important.pdf"
  }
}
```

### Request Response

```json
{
    "validationReportaDataHandler": "PD94bWwgdmVyc2lvbj0...",
    "DiagnosticData": {
        "DocumentName": "important.pdf",
        "ValidationDate": "2020-11-10T16:46:18",
        "ContainerInfo": null,
        "Signature": [
            {
                "Id": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                "DAIdentifier": null,
                "SignatureFilename": "important.pdf",
                "ErrorMessage": null,
                "ClaimedSigningTime": "2020-11-10T16:36:37",
                "SignatureFormat": "PAdES-BASELINE-T",
                "StructuralValidation": {
                    "Valid": true,
                    "Message": null
                },
                "DigestMatcher": [
                    {
                        "DataFound": true,
                        "DataIntact": true,
                        "DigestMethod": "SHA256",
                        "DigestValue": "y59i6ke9/3n7KDy79zFE+GneN5vztKHPTKqvRKJ5f3U=",
                        "match": null,
                        "type": "MESSAGE_DIGEST",
                        "name": null
                    }
                ],
                "BasicSignature": {
                    "EncryptionAlgoUsedToSignThisToken": "RSA",
                    "KeyLengthUsedToSignThisToken": "2048",
                    "DigestAlgoUsedToSignThisToken": "SHA256",
                    "MaskGenerationFunctionUsedToSignThisToken": null,
                    "SignatureIntact": false,
                    "SignatureValid": false
                },
                "SigningCertificate": {
                    "PublicKey": null,
                    "Certificate": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C"
                },
                "ChainItem": [
                    {
                        "Certificate": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C"
                    }
                ],
                "ContentType": "1.2.840.113549.1.7.1",
                "MimeType": null,
                "ContentIdentifier": null,
                "ContentHints": null,
                "SignatureProductionPlace": null,
                "CommitmentTypeIndication": [],
                "SignerRole": [],
                "Policy": {
                    "Id": "IMPLICIT_POLICY",
                    "Url": null,
                    "Description": null,
                    "Notice": null,
                    "ZeroHash": false,
                    "DigestAlgoAndValue": null,
                    "Asn1Processable": false,
                    "Identified": false,
                    "Status": false,
                    "ProcessingError": null,
                    "DigestAlgorithmsEqual": false,
                    "DocumentationReference": null
                },
                "SignerInfo": [
                    {
                        "IssuerName": "O=DSS-test, CN=RootSelfSignedFake",
                        "SerialNumber": 51497007561559,
                        "Ski": null,
                        "Current": true
                    }
                ],
                "PDFRevision": {
                    "SignatureFieldName": [
                        "Signature1"
                    ],
                    "PDFSignatureDictionary": {
                        "SignerName": null,
                        "Type": "Sig",
                        "Filter": "Adobe.PPKLite",
                        "SubFilter": "ETSI.CAdES.detached",
                        "ContactInfo": null,
                        "Reason": null,
                        "SignatureByteRange": [
                            0,
                            9391,
                            47281,
                            493
                        ]
                    }
                },
                "SignerDocumentRepresentations": {
                    "HashOnly": false,
                    "DocHashOnly": false
                },
                "FoundCertificates": {
                    "RelatedCertificate": [
                        {
                            "Origin": [
                                "SIGNED_DATA"
                            ],
                            "CertificateRef": [
                                {
                                    "Origin": "SIGNING_CERTIFICATE",
                                    "IssuerSerial": {
                                        "value": "MD4wNKQyMDAxGzAZBgNVBAMMElJvb3RTZWxmU2lnbmVkRmFrZTERMA8GA1UECgwIRFNTLXRlc3QCBi7WFNe7Vw==",
                                        "match": true
                                    },
                                    "DigestAlgoAndValue": {
                                        "DigestMethod": "SHA256",
                                        "DigestValue": "AvPrygFjJ0JTvICdJ0mN1BuwMW1+awZpYBFd4VVYnZw=",
                                        "match": true
                                    },
                                    "SerialInfo": null
                                }
                            ],
                            "Certificate": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C"
                        }
                    ],
                    "OrphanCertificate": []
                },
                "FoundRevocations": {
                    "RelatedRevocation": [],
                    "OrphanRevocation": []
                },
                "FoundTimestamp": [
                    {
                        "Timestamp": "T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7",
                        "Location": "DOC_TIMESTAMP"
                    }
                ],
                "SignatureScope": [
                    {
                        "Scope": "PARTIAL",
                        "Name": "Partial PDF",
                        "Description": "The document ByteRange : [0, 9391, 47281, 493]",
                        "Transformation": null,
                        "SignerData": "D-2D58AAA29DC086066B313EA6DCC9D43BC9D2AB3E4A4A775F9FD8F3C21EEC7031"
                    }
                ],
                "SignatureDigestReference": {
                    "CanonicalizationMethod": null,
                    "DigestMethod": "SHA256",
                    "DigestValue": "EY8QsDccdLEi7y4UIOilFTXOjdfpfps4auYR6oHqa2A="
                },
                "SignatureValue": "IWbu5VDt+AiOwzPY+M4GPWUmcmXUkcGX6dkf45C+oi9Cx3rbfFySOEEoxSM0vM3pXG347JsmKCZJhG+Elsh6dExJpSQg+Ks3OQFbObghTSHtOYZHKfM54pr8BLAD0kdYvGdqDPY+ZRvTYWMlZZnxzpUegh5JR+bypogV7hK6QnGJnIBDz7u6IBd4kun7pWiNN5QfRgxFBt2Nks/cgbgvgLofxKYAp3swOTSYaS4n46Dm422QzOlnyStV9iy2FDVrVfy/yni8WmEzF++juGPUQcGKhNqe+3plNZsNdC9NVLAwYGtk4QZ1ghfScXERqHa0/Aybi9am7u+DZu2N4rJ4Gw==",
                "CounterSignature": null,
                "Parent": null,
                "Duplicated": null
            }
        ],
        "Certificate": [
            {
                "Id": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C",
                "SubjectDistinguishedName": [
                    {
                        "value": "o=dss-test,cn=signerfake",
                        "Format": "CANONICAL"
                    },
                    {
                        "value": "O=DSS-test,CN=SignerFake",
                        "Format": "RFC2253"
                    }
                ],
                "IssuerDistinguishedName": [
                    {
                        "value": "o=dss-test,cn=rootselfsignedfake",
                        "Format": "CANONICAL"
                    },
                    {
                        "value": "O=DSS-test,CN=RootSelfSignedFake",
                        "Format": "RFC2253"
                    }
                ],
                "SerialNumber": 51497007561559,
                "SubjectSerialNumber": null,
                "CommonName": "SignerFake",
                "Locality": null,
                "State": null,
                "CountryName": null,
                "OrganizationIdentifier": null,
                "OrganizationName": "DSS-test",
                "GivenName": null,
                "OrganizationalUnit": null,
                "Surname": null,
                "Pseudonym": null,
                "Email": null,
                "subjectAlternativeName": null,
                "aiaUrl": [],
                "crlUrl": [],
                "ocspServerUrl": [],
                "Source": [
                    "SIGNATURE"
                ],
                "NotAfter": "2047-07-04T07:57:24",
                "NotBefore": "2017-06-08T11:26:01",
                "PublicKeySize": 2048,
                "PublicKeyEncryptionAlgo": "RSA",
                "EntityKey": "PK-3CFCA257859E202BCC83864D02B267B08A997C357AB98D923BBC63F00607C7B6",
                "KeyUsage": [
                    "keyCertSign",
                    "crlSign"
                ],
                "extendedKeyUsagesOid": [],
                "PSD2Info": null,
                "IdPkixOcspNoCheck": false,
                "BasicSignature": {
                    "EncryptionAlgoUsedToSignThisToken": "RSA",
                    "KeyLengthUsedToSignThisToken": "?",
                    "DigestAlgoUsedToSignThisToken": "SHA256",
                    "MaskGenerationFunctionUsedToSignThisToken": null,
                    "SignatureIntact": false,
                    "SignatureValid": false
                },
                "SigningCertificate": null,
                "ChainItem": [],
                "Trusted": false,
                "SelfSigned": false,
                "certificatePolicy": [],
                "qcStatementOid": [],
                "qcTypeOid": [],
                "SemanticsIdentifier": null,
                "TrustedServiceProvider": [],
                "CertificateRevocation": [],
                "Base64Encoded": null,
                "DigestAlgoAndValue": {
                    "DigestMethod": "SHA256",
                    "DigestValue": "AvPrygFjJ0JTvICdJ0mN1BuwMW1+awZpYBFd4VVYnZw=",
                    "match": null
                }
            },
            {
                "Id": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3",
                "SubjectDistinguishedName": [
                    {
                        "value": "c=lu,ou=pki-test,o=nowina solutions,cn=self-signed-tsa",
                        "Format": "CANONICAL"
                    },
                    {
                        "value": "C=LU,OU=PKI-TEST,O=Nowina Solutions,CN=self-signed-tsa",
                        "Format": "RFC2253"
                    }
                ],
                "IssuerDistinguishedName": [
                    {
                        "value": "c=lu,ou=pki-test,o=nowina solutions,cn=self-signed-tsa",
                        "Format": "CANONICAL"
                    },
                    {
                        "value": "C=LU,OU=PKI-TEST,O=Nowina Solutions,CN=self-signed-tsa",
                        "Format": "RFC2253"
                    }
                ],
                "SerialNumber": 100,
                "SubjectSerialNumber": null,
                "CommonName": "self-signed-tsa",
                "Locality": null,
                "State": null,
                "CountryName": "LU",
                "OrganizationIdentifier": null,
                "OrganizationName": "Nowina Solutions",
                "GivenName": null,
                "OrganizationalUnit": "PKI-TEST",
                "Surname": null,
                "Pseudonym": null,
                "Email": null,
                "subjectAlternativeName": null,
                "aiaUrl": [],
                "crlUrl": [],
                "ocspServerUrl": [],
                "Source": [
                    "TIMESTAMP"
                ],
                "NotAfter": "2021-05-11T04:52:19",
                "NotBefore": "2019-07-11T04:52:19",
                "PublicKeySize": 2048,
                "PublicKeyEncryptionAlgo": "RSA",
                "EntityKey": "PK-4A273B388E7DFD8A558EF79476F6E5060E065B0E8BCF13D7E5E93B722987E13D",
                "KeyUsage": [
                    "digitalSignature"
                ],
                "extendedKeyUsagesOid": [
                    {
                        "value": "1.3.6.1.5.5.7.3.8",
                        "Description": "timeStamping"
                    }
                ],
                "PSD2Info": null,
                "IdPkixOcspNoCheck": false,
                "BasicSignature": {
                    "EncryptionAlgoUsedToSignThisToken": "RSA",
                    "KeyLengthUsedToSignThisToken": "2048",
                    "DigestAlgoUsedToSignThisToken": "SHA256",
                    "MaskGenerationFunctionUsedToSignThisToken": null,
                    "SignatureIntact": true,
                    "SignatureValid": true
                },
                "SigningCertificate": null,
                "ChainItem": [],
                "Trusted": false,
                "SelfSigned": true,
                "certificatePolicy": [],
                "qcStatementOid": [],
                "qcTypeOid": [],
                "SemanticsIdentifier": null,
                "TrustedServiceProvider": [],
                "CertificateRevocation": null,
                "Base64Encoded": null,
                "DigestAlgoAndValue": {
                    "DigestMethod": "SHA256",
                    "DigestValue": "4XuHiC6u9q1AhKE7cuJ2HXawHXVEeWHKDz/qGpIyftM=",
                    "match": null
                }
            }
        ],
        "Revocation": [],
        "Timestamp": [
            {
                "Id": "T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7",
                "TimestampFilename": null,
                "ArchiveTimestampType": null,
                "ProductionTime": "2020-11-10T16:45:38",
                "DigestMatcher": [
                    {
                        "DataFound": true,
                        "DataIntact": true,
                        "DigestMethod": "SHA512",
                        "DigestValue": "ocZd9NVh3NQbn/MGp9ou0YysESB9XufDLIokjHDF2BScHtaXRzZPr1Rlff3ZfAY8KXQ6UmHukvZIa7sRG2r+vA==",
                        "match": null,
                        "type": "MESSAGE_IMPRINT",
                        "name": null
                    }
                ],
                "BasicSignature": {
                    "EncryptionAlgoUsedToSignThisToken": "RSA",
                    "KeyLengthUsedToSignThisToken": "2048",
                    "DigestAlgoUsedToSignThisToken": "SHA512",
                    "MaskGenerationFunctionUsedToSignThisToken": null,
                    "SignatureIntact": true,
                    "SignatureValid": true
                },
                "SigningCertificate": {
                    "PublicKey": null,
                    "Certificate": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3"
                },
                "ChainItem": [
                    {
                        "Certificate": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3"
                    }
                ],
                "SignerInfo": [
                    {
                        "IssuerName": "C=LU, OU=PKI-TEST, O=Nowina Solutions, CN=self-signed-tsa",
                        "SerialNumber": 100,
                        "Ski": null,
                        "Current": true
                    }
                ],
                "PDFRevision": {
                    "SignatureFieldName": [
                        "Signature2"
                    ],
                    "PDFSignatureDictionary": {
                        "SignerName": null,
                        "Type": "DocTimeStamp",
                        "Filter": "Adobe.PPKLite",
                        "SubFilter": "ETSI.RFC3161",
                        "ContactInfo": null,
                        "Reason": null,
                        "SignatureByteRange": [
                            0,
                            48174,
                            67120,
                            794
                        ]
                    }
                },
                "FoundCertificates": {
                    "RelatedCertificate": [
                        {
                            "Origin": [
                                "SIGNED_DATA"
                            ],
                            "CertificateRef": [
                                {
                                    "Origin": "SIGNING_CERTIFICATE",
                                    "IssuerSerial": null,
                                    "DigestAlgoAndValue": {
                                        "DigestMethod": "SHA512",
                                        "DigestValue": "D+vlpyV/YfFzThtGtciFjVreab2TxQWNqCzG+0kGa3Vwt5hCDx6ypZuWvh+fq6v0vZ3NojECzYCpSZuYBOS6aQ==",
                                        "match": true
                                    },
                                    "SerialInfo": null
                                }
                            ],
                            "Certificate": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3"
                        }
                    ],
                    "OrphanCertificate": []
                },
                "FoundRevocations": {
                    "RelatedRevocation": [],
                    "OrphanRevocation": []
                },
                "TimestampedObject": [
                    {
                        "Token": "D-2D58AAA29DC086066B313EA6DCC9D43BC9D2AB3E4A4A775F9FD8F3C21EEC7031",
                        "Category": "SIGNED_DATA"
                    },
                    {
                        "Token": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                        "Category": "SIGNATURE"
                    },
                    {
                        "Token": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C",
                        "Category": "CERTIFICATE"
                    }
                ],
                "Base64Encoded": null,
                "DigestAlgoAndValue": {
                    "DigestMethod": "SHA256",
                    "DigestValue": "BBB0OIee91oBKd+Chkp79RzwUI6MccivsvFtiG4Oscc=",
                    "match": null
                },
                "Type": "SIGNATURE_TIMESTAMP"
            }
        ],
        "OrphanTokens": null,
        "SignerData": [
            {
                "Id": "D-2D58AAA29DC086066B313EA6DCC9D43BC9D2AB3E4A4A775F9FD8F3C21EEC7031",
                "ReferencedName": "Partial PDF",
                "DigestAlgoAndValue": {
                    "DigestMethod": "SHA256",
                    "DigestValue": "Ci0okC0J7WNDL0zdCVlJ8Qwj88RLnynaVkC6+dQP16g=",
                    "match": null
                }
            }
        ],
        "TrustedList": []
    },
    "SimpleReport": {
        "ValidationPolicy": {
            "PolicyName": "QES AdESQC TL based",
            "PolicyDescription": "Validate electronic signatures and indicates whether they are Advanced electronic Signatures (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a\n\t\tQualified electronic Signature (QES). All certificates and their related chains supporting the signatures are validated against the EU Member State Trusted Lists (this includes\n\t\tsigner's certificate and certificates used to validate certificate validity status services - CRLs, OCSP, and time-stamps).\n\t"
        },
        "DocumentName": "important.pdf",
        "ValidSignaturesCount": 0,
        "SignaturesCount": 1,
        "ContainerType": null,
        "signatureOrTimestamp": [
            {
                "Signature": {
                    "SigningTime": "2020-11-10T16:36:37",
                    "BestSignatureTime": "2020-11-10T16:46:18",
                    "SignedBy": "SignerFake",
                    "SignatureLevel": {
                        "value": "N/A",
                        "description": "Not applicable"
                    },
                    "SignatureScope": [
                        {
                            "value": "The document ByteRange : [0, 9391, 47281, 493]",
                            "name": "Partial PDF",
                            "scope": "PARTIAL"
                        }
                    ],
                    "Filename": null,
                    "CertificateChain": {
                        "Certificate": [
                            {
                                "id": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C",
                                "qualifiedName": "SignerFake"
                            }
                        ]
                    },
                    "Indication": "TOTAL_FAILED",
                    "SubIndication": "SIG_CRYPTO_FAILURE",
                    "Errors": [
                        "Unable to build a certificate chain until a trusted list!",
                        "The result of the LTV validation process is not acceptable to continue the process!",
                        "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                        "The signature is not intact!",
                        "The result of the timestamps validation process is not conclusive!",
                        "The certificate chain for timestamp is not trusted, it does not contain a trust anchor."
                    ],
                    "Warnings": [
                        "The signature/seal is not a valid AdES digital signature!"
                    ],
                    "Infos": [],
                    "Id": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                    "CounterSignature": null,
                    "ParentId": null,
                    "SignatureFormat": "PAdES-BASELINE-T",
                    "ExtensionPeriodMin": null,
                    "ExtensionPeriodMax": null
                }
            }
        ],
        "Semantic": null,
        "ValidationTime": "2020-11-10T16:46:18"
    },
    "DetailedReport": {
        "signatureOrTimestampOrCertificate": [
            {
                "Signature": {
                    "ValidationProcessBasicSignature": {
                        "Constraint": [
                            {
                                "Name": {
                                    "value": "Is the result of the Basic Validation Process conclusive?",
                                    "NameId": "ADEST_ROBVPIIC"
                                },
                                "Status": "NOT OK",
                                "Error": {
                                    "value": "The result of the Basic validation process is not conclusive!",
                                    "NameId": "ADEST_ROBVPIIC_ANS"
                                },
                                "Warning": null,
                                "Info": null,
                                "AdditionalInfo": null,
                                "Id": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA"
                            }
                        ],
                        "Conclusion": {
                            "Indication": "FAILED",
                            "SubIndication": "SIG_CRYPTO_FAILURE",
                            "Errors": [
                                {
                                    "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                                    "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                                },
                                {
                                    "value": "The signature is not intact!",
                                    "NameId": "BBB_CV_ISI_ANS"
                                }
                            ],
                            "Warnings": [],
                            "Infos": []
                        },
                        "Title": "Validation Process for Basic Signatures",
                        "ProofOfExistence": {
                            "Time": "2020-11-10T16:46:18",
                            "TimestampId": null
                        }
                    },
                    "Timestamp": [
                        {
                            "ValidationProcessTimestamp": {
                                "Constraint": [
                                    {
                                        "Name": {
                                            "value": "Is the result of the timestamps validation process conclusive?",
                                            "NameId": "ADEST_ROTVPIIC"
                                        },
                                        "Status": "NOT OK",
                                        "Error": {
                                            "value": "The result of the timestamps validation process is not conclusive!",
                                            "NameId": "ADEST_ROTVPIIC_ANS"
                                        },
                                        "Warning": null,
                                        "Info": null,
                                        "AdditionalInfo": null,
                                        "Id": "T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7"
                                    }
                                ],
                                "Conclusion": {
                                    "Indication": "INDETERMINATE",
                                    "SubIndication": "NO_CERTIFICATE_CHAIN_FOUND",
                                    "Errors": [
                                        {
                                            "value": "The certificate chain for timestamp is not trusted, it does not contain a trust anchor.",
                                            "NameId": "BBB_XCV_CCCBB_TSP_ANS"
                                        }
                                    ],
                                    "Warnings": [],
                                    "Infos": []
                                },
                                "Title": "Validation Process for Timestamps",
                                "Type": "SIGNATURE_TIMESTAMP",
                                "ProductionTime": "2020-11-10T16:45:38"
                            },
                            "ValidationTimestampQualification": {
                                "Constraint": [
                                    {
                                        "Name": {
                                            "value": "Has a trusted list been reached for the certificate chain?",
                                            "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED"
                                        },
                                        "Status": "NOT OK",
                                        "Error": {
                                            "value": "Unable to build a certificate chain until a trusted list!",
                                            "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED_ANS"
                                        },
                                        "Warning": null,
                                        "Info": null,
                                        "AdditionalInfo": null,
                                        "Id": null
                                    }
                                ],
                                "Conclusion": {
                                    "Indication": "FAILED",
                                    "SubIndication": null,
                                    "Errors": [
                                        {
                                            "value": "Unable to build a certificate chain until a trusted list!",
                                            "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED_ANS"
                                        }
                                    ],
                                    "Warnings": [],
                                    "Infos": []
                                },
                                "Title": "Timestamp Qualification",
                                "TimestampQualification": "N/A"
                            },
                            "Id": "T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7"
                        }
                    ],
                    "ValidationProcessLongTermData": {
                        "Constraint": [
                            {
                                "Name": {
                                    "value": "Is the result of the Basic Validation Process acceptable?",
                                    "NameId": "LTV_ABSV"
                                },
                                "Status": "NOT OK",
                                "Error": {
                                    "value": "The result of the Basic validation process is not acceptable to continue the process!",
                                    "NameId": "LTV_ABSV_ANS"
                                },
                                "Warning": null,
                                "Info": null,
                                "AdditionalInfo": null,
                                "Id": null
                            }
                        ],
                        "Conclusion": {
                            "Indication": "FAILED",
                            "SubIndication": "SIG_CRYPTO_FAILURE",
                            "Errors": [
                                {
                                    "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                                    "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                                },
                                {
                                    "value": "The signature is not intact!",
                                    "NameId": "BBB_CV_ISI_ANS"
                                }
                            ],
                            "Warnings": [],
                            "Infos": []
                        },
                        "Title": "Validation Process for Signatures with Time and Signatures with Long-Term Validation Data",
                        "ProofOfExistence": {
                            "Time": "2020-11-10T16:46:18",
                            "TimestampId": null
                        }
                    },
                    "ValidationProcessArchivalData": {
                        "Constraint": [
                            {
                                "Name": {
                                    "value": "Is the result of the LTV validation process acceptable?",
                                    "NameId": "ARCH_LTVV"
                                },
                                "Status": "NOT OK",
                                "Error": {
                                    "value": "The result of the LTV validation process is not acceptable to continue the process!",
                                    "NameId": "ARCH_LTVV_ANS"
                                },
                                "Warning": null,
                                "Info": null,
                                "AdditionalInfo": null,
                                "Id": null
                            }
                        ],
                        "Conclusion": {
                            "Indication": "FAILED",
                            "SubIndication": "SIG_CRYPTO_FAILURE",
                            "Errors": [
                                {
                                    "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                                    "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                                },
                                {
                                    "value": "The signature is not intact!",
                                    "NameId": "BBB_CV_ISI_ANS"
                                }
                            ],
                            "Warnings": [],
                            "Infos": []
                        },
                        "Title": "Validation Process for Signatures with Archival Data",
                        "ProofOfExistence": {
                            "Time": "2020-11-10T16:46:18",
                            "TimestampId": null
                        }
                    },
                    "ValidationSignatureQualification": {
                        "ValidationCertificateQualification": [],
                        "Constraint": [
                            {
                                "Name": {
                                    "value": "Is the signature/seal an acceptable AdES digital signature (ETSI EN 319 102-1)?",
                                    "NameId": "QUAL_IS_ADES"
                                },
                                "Status": "WARNING",
                                "Error": null,
                                "Warning": {
                                    "value": "The signature/seal is not a valid AdES digital signature!",
                                    "NameId": "QUAL_IS_ADES_INV"
                                },
                                "Info": null,
                                "AdditionalInfo": null,
                                "Id": null
                            },
                            {
                                "Name": {
                                    "value": "Has a trusted list been reached for the certificate chain?",
                                    "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED"
                                },
                                "Status": "NOT OK",
                                "Error": {
                                    "value": "Unable to build a certificate chain until a trusted list!",
                                    "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED_ANS"
                                },
                                "Warning": null,
                                "Info": null,
                                "AdditionalInfo": null,
                                "Id": null
                            }
                        ],
                        "Conclusion": {
                            "Indication": "FAILED",
                            "SubIndication": null,
                            "Errors": [
                                {
                                    "value": "Unable to build a certificate chain until a trusted list!",
                                    "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED_ANS"
                                },
                                {
                                    "value": "Unable to build a certificate chain until a trusted list!",
                                    "NameId": "QUAL_CERT_TRUSTED_LIST_REACHED_ANS"
                                }
                            ],
                            "Warnings": [
                                {
                                    "value": "The signature/seal is not a valid AdES digital signature!",
                                    "NameId": "QUAL_IS_ADES_INV"
                                }
                            ],
                            "Infos": []
                        },
                        "Title": "Signature Qualification",
                        "SignatureQualification": "N/A"
                    },
                    "Id": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                    "CounterSignature": null
                }
            }
        ],
        "BasicBuildingBlocks": [
            {
                "FC": null,
                "ISC": {
                    "CertificateChain": {
                        "ChainItem": [
                            {
                                "Source": "TIMESTAMP",
                                "Id": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3"
                            }
                        ]
                    },
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Is there an identified candidate for the signing certificate?",
                                "NameId": "BBB_ICS_ISCI"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'signing-certificate' present?",
                                "NameId": "BBB_ICS_ISASCP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'signing-certificate' present only once?",
                                "NameId": "BBB_ICS_ISASCPU"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'cert-digest' of the certificate present?",
                                "NameId": "BBB_ICS_ISACDP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Does the certificate digest value match a digest value found in the certificate reference(s)?",
                                "NameId": "BBB_ICS_ICDVV"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": [],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Identification of the Signing Certificate"
                },
                "VCI": null,
                "XCV": {
                    "SubXCV": [],
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Can the certificate chain be built till a trust anchor?",
                                "NameId": "BBB_XCV_CCCBB"
                            },
                            "Status": "NOT OK",
                            "Error": {
                                "value": "The certificate chain for timestamp is not trusted, it does not contain a trust anchor.",
                                "NameId": "BBB_XCV_CCCBB_TSP_ANS"
                            },
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "INDETERMINATE",
                        "SubIndication": "NO_CERTIFICATE_CHAIN_FOUND",
                        "Errors": [
                            {
                                "value": "The certificate chain for timestamp is not trusted, it does not contain a trust anchor.",
                                "NameId": "BBB_XCV_CCCBB_TSP_ANS"
                            }
                        ],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "X509 Certificate Validation"
                },
                "CV": {
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Has the reference data object been found?",
                                "NameId": "BBB_CV_IRDOF"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Reference : MESSAGE_IMPRINT",
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the reference data object intact?",
                                "NameId": "BBB_CV_IRDOI"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Reference : MESSAGE_IMPRINT",
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is timestamp's signature intact?",
                                "NameId": "BBB_CV_ISIT"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Id = T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7",
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": [],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Cryptographic Verification"
                },
                "SAV": {
                    "CryptographicInfo": {
                        "Algorithm": "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512",
                        "KeyLength": "2048",
                        "Secure": true,
                        "NotAfter": "2022-12-31T22:00:00"
                    },
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Are timestamp cryptographic constraints met?",
                                "NameId": "ATCCM"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Validation time : 2020-11-10 16:46 for token with ID : [T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7]",
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": [],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Signature Acceptance Validation",
                    "ValidationTime": "2020-11-10T16:46:18"
                },
                "PSV": null,
                "PCV": null,
                "VTS": null,
                "CertificateChain": {
                    "ChainItem": [
                        {
                            "Source": "TIMESTAMP",
                            "Id": "C-E17B87882EAEF6AD4084A13B72E2761D76B01D75447961CA0F3FEA1A92327ED3"
                        }
                    ]
                },
                "Conclusion": {
                    "Indication": "INDETERMINATE",
                    "SubIndication": "NO_CERTIFICATE_CHAIN_FOUND",
                    "Errors": [
                        {
                            "value": "The certificate chain for timestamp is not trusted, it does not contain a trust anchor.",
                            "NameId": "BBB_XCV_CCCBB_TSP_ANS"
                        }
                    ],
                    "Warnings": null,
                    "Infos": null
                },
                "Id": "T-04107438879EF75A0129DF82864A7BF51CF0508E8C71C8AFB2F16D886E0EB1C7",
                "Type": "TIMESTAMP"
            },
            {
                "FC": {
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Does the signature format correspond to an expected format?",
                                "NameId": "BBB_FC_IEFF"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signature identification not ambiguous?",
                                "NameId": "BBB_FC_ISD"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is only one SignerInfo present?",
                                "NameId": "BBB_FC_IOSIP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": null,
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Format Checking"
                },
                "ISC": {
                    "CertificateChain": {
                        "ChainItem": [
                            {
                                "Source": "SIGNATURE",
                                "Id": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C"
                            }
                        ]
                    },
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Is there an identified candidate for the signing certificate?",
                                "NameId": "BBB_ICS_ISCI"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'signing-certificate' present?",
                                "NameId": "BBB_ICS_ISASCP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'signing-certificate' present only once?",
                                "NameId": "BBB_ICS_ISASCPU"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed attribute: 'cert-digest' of the certificate present?",
                                "NameId": "BBB_ICS_ISACDP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Does the certificate digest value match a digest value found in the certificate reference(s)?",
                                "NameId": "BBB_ICS_ICDVV"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Are the issuer distinguished name and the serial number equal?",
                                "NameId": "BBB_ICS_AIDNASNE"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": null,
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Identification of the Signing Certificate"
                },
                "VCI": {
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Is the signature policy known?",
                                "NameId": "BBB_VCI_ISPK"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": null,
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Validation Context Initialization"
                },
                "XCV": {
                    "SubXCV": [],
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Can the certificate chain be built till a trust anchor?",
                                "NameId": "BBB_XCV_CCCBB"
                            },
                            "Status": "NOT OK",
                            "Error": {
                                "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                                "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                            },
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "INDETERMINATE",
                        "SubIndication": "NO_CERTIFICATE_CHAIN_FOUND",
                        "Errors": [
                            {
                                "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                                "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                            }
                        ],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "X509 Certificate Validation"
                },
                "CV": {
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Has the reference data object been found?",
                                "NameId": "BBB_CV_IRDOF"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Reference : MESSAGE_DIGEST",
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the reference data object intact?",
                                "NameId": "BBB_CV_IRDOI"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Reference : MESSAGE_DIGEST",
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signature intact?",
                                "NameId": "BBB_CV_ISI"
                            },
                            "Status": "NOT OK",
                            "Error": {
                                "value": "The signature is not intact!",
                                "NameId": "BBB_CV_ISI_ANS"
                            },
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Id = S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "FAILED",
                        "SubIndication": "SIG_CRYPTO_FAILURE",
                        "Errors": [
                            {
                                "value": "The signature is not intact!",
                                "NameId": "BBB_CV_ISI_ANS"
                            }
                        ],
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Cryptographic Verification"
                },
                "SAV": {
                    "CryptographicInfo": {
                        "Algorithm": "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
                        "KeyLength": "2048",
                        "Secure": true,
                        "NotAfter": "2022-12-31T22:00:00"
                    },
                    "Constraint": [
                        {
                            "Name": {
                                "value": "Is the structure of the signature valid?",
                                "NameId": "BBB_SAV_ISSV"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed qualifying property: 'signing-time' present?",
                                "NameId": "BBB_SAV_ISQPSTP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Is the signed qualifying property: 'message-digest' or 'SignedProperties' present?",
                                "NameId": "BBB_SAV_ISQPMDOSPP"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": null,
                            "Id": null
                        },
                        {
                            "Name": {
                                "value": "Are signature cryptographic constraints met?",
                                "NameId": "ASCCM"
                            },
                            "Status": "OK",
                            "Error": null,
                            "Warning": null,
                            "Info": null,
                            "AdditionalInfo": "Validation time : 2020-11-10 16:46 for token with ID : [S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA]",
                            "Id": null
                        }
                    ],
                    "Conclusion": {
                        "Indication": "PASSED",
                        "SubIndication": null,
                        "Errors": null,
                        "Warnings": [],
                        "Infos": []
                    },
                    "Title": "Signature Acceptance Validation",
                    "ValidationTime": "2020-11-10T16:46:18"
                },
                "PSV": null,
                "PCV": null,
                "VTS": null,
                "CertificateChain": {
                    "ChainItem": [
                        {
                            "Source": "SIGNATURE",
                            "Id": "C-02F3EBCA0163274253BC809D27498DD41BB0316D7E6B066960115DE155589D9C"
                        }
                    ]
                },
                "Conclusion": {
                    "Indication": "FAILED",
                    "SubIndication": "SIG_CRYPTO_FAILURE",
                    "Errors": [
                        {
                            "value": "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                            "NameId": "BBB_XCV_CCCBB_SIG_ANS"
                        },
                        {
                            "value": "The signature is not intact!",
                            "NameId": "BBB_CV_ISI_ANS"
                        }
                    ],
                    "Warnings": null,
                    "Infos": null
                },
                "Id": "S-3C70522FD9A46A58A1B85628826071C464AC88902C2EA15B2BD3C5285069E0EA",
                "Type": "SIGNATURE"
            }
        ],
        "TLAnalysis": [],
        "ValidationTime": "2020-11-10T16:46:18"
    }
}
```
