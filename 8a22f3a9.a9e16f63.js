(window.webpackJsonp=window.webpackJsonp||[]).push([[5],{73:function(e,t,r){"use strict";r.r(t),r.d(t,"frontMatter",(function(){return o})),r.d(t,"metadata",(function(){return b})),r.d(t,"toc",(function(){return s})),r.d(t,"default",(function(){return i}));var n=r(3),a=r(7),l=(r(0),r(88)),o={id:"errors",title:"API Errors"},b={unversionedId:"errors",id:"errors",isDocsHomePage:!1,title:"API Errors",description:"In case of Error during handling user\u2019s request the API responds using the following schema:",source:"@site/docs/errors.md",slug:"/errors",permalink:"/gr.grnet.eseal/docs/errors",version:"current",sidebar:"someSidebar",previous:{title:"PDF Validation API Calls",permalink:"/gr.grnet.eseal/docs/validation"},next:{title:"timestamp",permalink:"/gr.grnet.eseal/docs/timestamp"}},s=[{value:"Error Codes",id:"error-codes",children:[]}],d={toc:s};function i(e){var t=e.components,r=Object(a.a)(e,["components"]);return Object(l.b)("wrapper",Object(n.a)({},d,r,{components:t,mdxType:"MDXLayout"}),Object(l.b)("p",null,"In case of Error during handling user\u2019s request the API responds using the following schema:"),Object(l.b)("pre",null,Object(l.b)("code",{parentName:"pre",className:"language-json"},'{\n   "error": {\n      "code": 500,\n      "message": "Something bad happened",\n      "status": "INTERNAL"\n   }\n}\n')),Object(l.b)("h2",{id:"error-codes"},"Error Codes"),Object(l.b)("table",null,Object(l.b)("thead",{parentName:"table"},Object(l.b)("tr",{parentName:"thead"},Object(l.b)("th",{parentName:"tr",align:null},"Message"),Object(l.b)("th",{parentName:"tr",align:null},"Code"),Object(l.b)("th",{parentName:"tr",align:null},"Status"),Object(l.b)("th",{parentName:"tr",align:null},"Details"))),Object(l.b)("tbody",{parentName:"table"},Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",{parentName:"tr",align:null},"Malformed JSON body"),Object(l.b)("td",{parentName:"tr",align:null},"400"),Object(l.b)("td",{parentName:"tr",align:null},"BAD_REQUEST"),Object(l.b)("td",{parentName:"tr",align:null},"The request body does not represent a valid json.")),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",{parentName:"tr",align:null},"Field ",Object(l.b)("inlineCode",{parentName:"td"},"<>")," cannot be empty"),Object(l.b)("td",{parentName:"tr",align:null},"400"),Object(l.b)("td",{parentName:"tr",align:null},"BAD_REQUEST"),Object(l.b)("td",{parentName:"tr",align:null},"The request does not contain a required field.")),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",{parentName:"tr",align:null},"Wrong user credentials"),Object(l.b)("td",{parentName:"tr",align:null},"422"),Object(l.b)("td",{parentName:"tr",align:null},"UNPROCESSABLE_ENTITY"),Object(l.b)("td",{parentName:"tr",align:null},"Wrong username or password is being used when trying to access the remote provider http api.")),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",{parentName:"tr",align:null},"Invalid key or expired TOTP"),Object(l.b)("td",{parentName:"tr",align:null},"422"),Object(l.b)("td",{parentName:"tr",align:null},"UNPROCESSABLE_ENTITY"),Object(l.b)("td",{parentName:"tr",align:null},"The provided key does not match the eky that corresponds to the used username/password pair OR the totp that was automatically generated timed out and you should retry.")),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",{parentName:"tr",align:null},"Internal server error"),Object(l.b)("td",{parentName:"tr",align:null},"500"),Object(l.b)("td",{parentName:"tr",align:null},"INTERNAL_SERVER_ERROR"),Object(l.b)("td",{parentName:"tr",align:null},"Internal error that is out of the scope of the client user.")))))}i.isMDXComponent=!0}}]);