# C2DM example

Client side example for C2DM.

## C2DM APIs

You can get `auth` token by doing this:

```
curl https://www.google.com/accounts/ClientLogin -d \
	 Email=your_user -d "Passwd=your_password" -d accountType=GOOGLE \
	 -d source=Google-cURL-Example -d service=ac2dm
```

Then connect your Android phone to your Computer and pick up `registration_id` from Logcat.

```
curl --header "Authorization: GoogleLogin auth=your_authenticationid" \
	 "https://android.apis.google.com/c2dm/send" -d registration_id=your_registration \
	 -d "data.payload=MessagePushed" -d collapse_key=0
```
