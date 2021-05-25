(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[328],{3905:function(e,t,n){"use strict";n.d(t,{Zo:function(){return m},kt:function(){return c}});var a=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var p=a.createContext({}),s=function(e){var t=a.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},m=function(e){var t=s(e.components);return a.createElement(p.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,p=e.parentName,m=l(e,["components","mdxType","originalType","parentName"]),d=s(n),c=r,k=d["".concat(p,".").concat(c)]||d[c]||u[c]||i;return n?a.createElement(k,o(o({ref:t},m),{},{components:n})):a.createElement(k,o({ref:t},m))}));function c(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,o=new Array(i);o[0]=d;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l.mdxType="string"==typeof e?e:r,o[1]=l;for(var s=2;s<i;s++)o[s]=n[s];return a.createElement.apply(null,o)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},5849:function(e,t,n){"use strict";n.r(t),n.d(t,{frontMatter:function(){return o},metadata:function(){return l},toc:function(){return p},default:function(){return m}});var a=n(2122),r=n(9756),i=(n(7294),n(3905)),o={id:"postman",title:"Communicate with E-Signature API",sidebar_label:"Communicate with E-Signature API",keywords:["eseal","e-seal","validation","api","doc","docs","documentation","documents","pdf","grnet","postman","json"]},l={unversionedId:"postman",id:"postman",isDocsHomePage:!1,title:"Communicate with E-Signature API",description:"Prerequisites",source:"@site/docs/postman.md",sourceDirName:".",slug:"/postman",permalink:"/gr.grnet.eseal/docs/postman",version:"current",sidebar_label:"Communicate with E-Signature API",frontMatter:{id:"postman",title:"Communicate with E-Signature API",sidebar_label:"Communicate with E-Signature API",keywords:["eseal","e-seal","validation","api","doc","docs","documentation","documents","pdf","grnet","postman","json"]}},p=[{value:"Prerequisites",id:"prerequisites",children:[]},{value:"Instructions",id:"instructions",children:[]},{value:"PDF Signing",id:"pdf-signing",children:[]},{value:"PDF Timestamp",id:"pdf-timestamp",children:[]},{value:"PDF Validation",id:"pdf-validation",children:[]}],s={toc:p};function m(e){var t=e.components,n=(0,r.Z)(e,["components"]);return(0,i.kt)("wrapper",(0,a.Z)({},s,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h3",{id:"prerequisites"},"Prerequisites"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Postman")),(0,i.kt)("h3",{id:"instructions"},"Instructions"),(0,i.kt)("p",null,"In order to communicate with E-Signature Rest API the following postman components should both be imported on Postman platform."),(0,i.kt)("a",{target:"_blank",href:"/assets/e_signature_rest_api.postman_collection.json"},"Postman Collection"),(0,i.kt)("a",{target:"_blank",href:"/assets/eseal_environment.postman_environment.json"},"Postman Environment"),(0,i.kt)("p",null,"Into Postman Collection, you can find the  variables which are important in order to execute requests on API."),(0,i.kt)("p",null,"The following variables are predefined and should not be changed."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},'    {\n        "key": "signing_path",\n        "value": "api/v1/signing"\n    },\n    {\n        "key": "signing_endpoint",\n        "value": "remoteSignDocumentDetached"\n    },\n    {\n        "key": "validation_path",\n        "value": "api/v1/validation"\n    },\n    {\n        "key": "validation_endpoint",\n        "value": "validateDocument"\n    },\n    {\n        "key": "timestamp_path",\n        "value": "api/v1/timestamping"\n    },\n    {\n        "key": "timestamp_endpoint",\n        "value": "remoteTimestampDocument"\n    }\n')),(0,i.kt)("p",null,"The following variables are empty and should be filled in approprielly. The ",(0,i.kt)("inlineCode",{parentName:"p"},"username"),", ",(0,i.kt)("inlineCode",{parentName:"p"},"password")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"key")," will be provided by GRNET and the rest variables should be filled in by user."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},'    {\n        "key": "username",\n        "value": ""\n    },\n    {\n        "key": "password",\n        "value": ""\n    },\n    {\n        "key": "key",\n        "value": ""\n    },\n    {\n        "key": "pdf_base64_to_sign",\n        "value": ""\n    },\n    {\n        "key": "pdf_base64_to_validate",\n        "value": ""\n    },\n    {\n        "key": "pdf_base64_to_timestamp",\n        "value": ""\n    }\n')),(0,i.kt)("p",null,"Into Postman Environment, you can find the variables which are important in order to communicate with the API of GRNET. "),(0,i.kt)("p",null,"The following variables are predefined and should not be changed."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},'    {\n        "key": "base_url",\n        "value": "eseal.devel.einfra.grnet.gr",\n        "enabled": true\n    },\n    {\n        "key": "port",\n        "value": "443",\n        "enabled": true\n    },\n    {\n        "key": "protocol",\n        "value": "https",\n        "enabled": true\n    }\n')),(0,i.kt)("p",null,"Finally , you can find the following 3 POST requests : "),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("a",{parentName:"p",href:"/gr.grnet.eseal/docs/signing#post---sign-a-pdf-document-detached"},(0,i.kt)("em",{parentName:"a"},"PDF Signing")))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("a",{parentName:"p",href:"/gr.grnet.eseal/docs/timestamp"},(0,i.kt)("em",{parentName:"a"},"PDF Timestamp")))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("a",{parentName:"p",href:"/gr.grnet.eseal/docs/validation"},(0,i.kt)("em",{parentName:"a"},"PDF Validation"))))),(0,i.kt)("h3",{id:"pdf-signing"},"PDF Signing"),(0,i.kt)("p",null,"In order to sign a pdf :"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("a",{parentName:"p",href:"https://base64.guru/converter/encode/pdf"},(0,i.kt)("em",{parentName:"a"},"Convert the pdf to Base64 format")))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("em",{parentName:"p"},"Fill in the variable pdf_base64_to_sign"))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("em",{parentName:"p"},"Execute the PDF Signing request")))),(0,i.kt)("p",null,(0,i.kt)("em",{parentName:"p"},"Response Body :")," "),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},'{\n"signedDocumentBytes": "JVBER..=="\n}')),(0,i.kt)("p",null,"In order to convert the ",(0,i.kt)("inlineCode",{parentName:"p"},"signedDocumentBytes")," to PDF you can use the following platform ",(0,i.kt)("a",{parentName:"p",href:"https://base64.guru/converter/decode/pdf"},"Base64 to PDF"),"."),(0,i.kt)("h3",{id:"pdf-timestamp"},"PDF Timestamp"),(0,i.kt)("p",null,"In order to timestamp a pdf :"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("a",{parentName:"p",href:"https://base64.guru/converter/encode/pdf"},(0,i.kt)("em",{parentName:"a"},"Convert the pdf to Base64 format")))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("em",{parentName:"p"},"Fill in the variable pdf_base64_to_timestamp"))),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("p",{parentName:"li"},(0,i.kt)("em",{parentName:"p"},"Execute the PDF Timestamp request")))),(0,i.kt)("p",null,(0,i.kt)("em",{parentName:"p"},"Response body :")),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},'{\n"timestampedDocumentBytes": "JVBERi0xL..."\n}')),(0,i.kt)("p",null,"In order to convert the ",(0,i.kt)("inlineCode",{parentName:"p"},"timestampedDocumentBytes")," to PDF you can use the following platform ",(0,i.kt)("a",{parentName:"p",href:"https://base64.guru/converter/decode/pdf"},"Base64 to PDF"),"."),(0,i.kt)("h3",{id:"pdf-validation"},"PDF Validation"),(0,i.kt)("p",null,"In order to validate a signed/timestamped pdf :"),(0,i.kt)("p",null,"1) ",(0,i.kt)("em",{parentName:"p"},"Get the signedDocumentBytes/timestampedDocumentBytes response parameter respectively")),(0,i.kt)("p",null,"2) ",(0,i.kt)("em",{parentName:"p"},"Fill in the variable pdf_base64_to_validate")),(0,i.kt)("p",null,"3) ",(0,i.kt)("em",{parentName:"p"},"Execute the PDF Validation request")))}m.isMDXComponent=!0}}]);