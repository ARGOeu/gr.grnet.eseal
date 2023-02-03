---
id: errors
title: API Errors
sidebar_position: 5
keywords:
  - eseal
  - e-seal
  - errors
  - api
  - doc
  - docs
  - documentation
  - documents
  - pdf
  - grnet
---


In case of Error during handling user’s request the API responds using the following schema:

```json
{
   "error": {
      "code": 500,
      "message": "Something bad happened",
      "status": "INTERNAL"
   }
}
```
## Error Codes

Message | Code | Status | Details
------|------|----------|------------------
Malformed JSON body | 400 | BAD_REQUEST | The request body does not represent a valid json.
Field `<>` cannot be empty | 400 | BAD_REQUEST | The request does not contain a required field.
Wrong user credentials | 422 | UNPROCESSABLE_ENTITY | Wrong username or password is being used when trying to access the remote provider http api.
Invalid key or expired TOTP | 422 | UNPROCESSABLE_ENTITY | The provided key does not match the key that corresponds to the used username/password pair OR the totp that was automatically generated timed out and you should retry.
Internal server error | 500 | INTERNAL_SERVER_ERROR | Internal error that is out of the scope of the client user.
Field `<>` should be encoded in base64 format | 400 | BAD_REQUEST | The request does not contain a required base64 encoded field
