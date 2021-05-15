(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{76:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return o})),n.d(t,"metadata",(function(){return m})),n.d(t,"toc",(function(){return i})),n.d(t,"default",(function(){return c}));var a=n(3),s=n(7),r=(n(0),n(88)),o={id:"timestamp",title:"PDF Timestamp API Calls",sidebar_label:"PDF Timestamp API Calls",keywords:["eseal","e-seal","timestamp","api","doc","docs","documentation","documents","pdf","grnet"]},m={unversionedId:"timestamp",id:"timestamp",isDocsHomePage:!1,title:"PDF Timestamp API Calls",description:"POST - Timestamp PDF Document",source:"@site/docs/timestamp.md",slug:"/timestamp",permalink:"/gr.grnet.eseal/docs/timestamp",version:"current",sidebar_label:"PDF Timestamp API Calls",sidebar:"someSidebar",previous:{title:"API Errors",permalink:"/gr.grnet.eseal/docs/errors"}},i=[{value:"POST - Timestamp PDF Document",id:"post---timestamp-pdf-document",children:[{value:"Request",id:"request",children:[]},{value:"Request Body",id:"request-body",children:[]},{value:"Response",id:"response",children:[]},{value:"Errors",id:"errors",children:[]}]}],p={toc:i};function c(e){var t=e.components,n=Object(s.a)(e,["components"]);return Object(r.b)("wrapper",Object(a.a)({},p,n,{components:t,mdxType:"MDXLayout"}),Object(r.b)("h2",{id:"post---timestamp-pdf-document"},"POST - Timestamp PDF Document"),Object(r.b)("h3",{id:"request"},"Request"),Object(r.b)("b",null,"POST")," @ ",Object(r.b)("i",null,"/api/v1/timestamping/remoteTimestampDocument"),Object(r.b)("h3",{id:"request-body"},"Request Body"),Object(r.b)("ul",null,Object(r.b)("li",{parentName:"ul"},Object(r.b)("p",{parentName:"li"},Object(r.b)("inlineCode",{parentName:"p"},"toTimestampDocument.bytes")," : The pdf document in base64 encoded format")),Object(r.b)("li",{parentName:"ul"},Object(r.b)("p",{parentName:"li"},Object(r.b)("inlineCode",{parentName:"p"},"toTimestampDocument.name")," : Placeholder name for the pdf document")),Object(r.b)("li",{parentName:"ul"},Object(r.b)("p",{parentName:"li"},Object(r.b)("inlineCode",{parentName:"p"},"tspSource(optional)")," : The timestamp server that generates the timestamped document.\nThe possible values are APED or HARICA. The default value is HARICA."))),Object(r.b)("pre",null,Object(r.b)("code",{parentName:"pre",className:"language-json"},'{\n  "tsaSource" : "APED",\n  "toTimestampDocument" : {\n    "bytes" : "JVBERi...",\n    "name" : "important.pdf"\n  }\n}\n')),Object(r.b)("h3",{id:"response"},"Response"),Object(r.b)("pre",null,Object(r.b)("code",{parentName:"pre",className:"language-json"},'{\n    "timestampedDocumentBytes": "JVBERi0xL..."\n}\n')),Object(r.b)("h3",{id:"errors"},"Errors"),Object(r.b)("p",null,"Please refer to section ",Object(r.b)("a",{parentName:"p",href:"/gr.grnet.eseal/docs/errors"},"Errors")," to see all possible Errors"))}c.isMDXComponent=!0}}]);