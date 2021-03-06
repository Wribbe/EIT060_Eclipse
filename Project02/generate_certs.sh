#!/bin/bash

create_caCert() {
    pass="$1"
    echo "Creating a selfigned cert"
    openssl req -x509 -newkey rsa:1024 -keyout "$cakey" -passout pass:$pass -out "$cacert" -subj "/CN=CA"
}

# Create truststore $1:name $2:password $3:alias
create_truststore()
{
    echo "Creating trust store: $1"
    keytool -import -v -file "$cacert" -alias "$3" -keystore "$1" -storepass "$2" <<EOD
yes
EOD
}

# Create keystore with $1:name $2:password $3:CN-string $4:username $5:alias
create_keystore() {

    echo "Create keystore with key-pairs"
    echo "keytool -alias $4 -genkeypair -keystore $1 -storepass $2 (0: $0 1: $1 2:$2 3:$3 4:$4)"
    keytool -alias "$5" -genkeypair -keystore "$1" -storepass "$2"<<EOD
$3
$4
.
.
.
.
yes
EOD
    echo "Import "$cacert" to $1"
    echo "keytool -import -alias CA  -file "$cacert" -keystore $1 -storepass $2 << EOD"
    keytool -import -alias CA -file "$cacert" -keystore "$1" -storepass "$2" << EOD
yes
EOD

}

# Create and sign a signreqest with $1:requestFileName $2:signedCertName $3:password $4:keystoreName $5: alias $6: username
create_and_sign_request() {

    echo "Creating sign-request"
    echo "keytool -alias $5 -certreq -file $1  -keystore $4 -storepass $3"
    keytool -alias "$5" -certreq -file "$1"  -keystore "$4" -storepass "$3"

    echo "Signing sign-request"
    echo "openssl x509 -req -in $1 -CA "$cacert" -CAkey "$cakey" -extfile v3.ext -CAcreateserial -out $2  pass:$3"
    openssl x509 -req -in "$1" -CA "$cacert" -CAkey "$cakey" -extfile v3.ext -CAcreateserial -out "$2" -passin pass:"$3"

    echo "Import signed request to $keystore"
    echo "keytool -import -alias "$5" -v -file "$2" -keystore "$4" -storepass "$3""
    keytool -import -alias "$5" -v -file "$2" -keystore "$4" -storepass "$3"
}

create_signed_keystore() {

    # input parameters.
    username="$1"
    password="$2"
    type="$3"

    # generated parameters.
    keypair_name="${username}_keypair"
    request_name="${username}_certrequest.pem"
    signed_request_name="${username}_signed.pem"

    echo "username: $username"
    echo "password: $password"
    echo "type: $type"
    echo "keypair_name: $keypair_name"
    echo "request_name: $request_name"
    echo "signed_request_name: $signed_request_name"

    create_keystore "stores/$username" "$password" "$type" "$username" "$keypair_name"
    create_and_sign_request "keys/$request_name" "keys/$signed_request_name" \
                            "$password" "stores/$username" "$keypair_name"
}

rm -rf stores
rm -rf keys

mkdir stores
mkdir keys

cakey="keys/ca_key.pem"
cacert="keys/ca_cert.pem"
ca_password="password"

password="password"
CN_String="<atn08sen>(StefanEng)/<dat12emu>(Erik Munkby)/<dic13sli>(Sara Lindgren)"


create_caCert $ca_password # Create CA-certificate.

create_truststore stores/clienttruststore $password CN
create_truststore stores/servertruststore $password CN

# create_signed_keystore username password user-type.
# creates a keystorefile named {username} signed by CA with CN={user-type}.

username="$1"
password="$2"
access="$3"

create_signed_keystore clientkeystore password user
create_signed_keystore serverkeystore password server

create_signed_keystore "Charlie Hultin" password doctor
create_signed_keystore "Sephirot Nilsson" password doctor
create_signed_keystore "Mary Persson" password doctor
create_signed_keystore "Jones Zoran" password doctor
create_signed_keystore "Ann Svensson" password doctor
create_signed_keystore "Svetlana Mercer" password doctor
create_signed_keystore "Peter Miller" password doctor

create_signed_keystore "Daniel Nilsson" password nurse
create_signed_keystore "Viktoria Zoran" password nurse
create_signed_keystore "Susann Mercer" password nurse
create_signed_keystore "Molly Hultin" password nurse
create_signed_keystore "Adam Persson" password nurse
create_signed_keystore "Victor Miller" password nurse
create_signed_keystore "Condrad Svensson" password nurse

create_signed_keystore "Mario Svensson" password user
create_signed_keystore "Emma Persson" password user
create_signed_keystore "Lisa Miller" password user
create_signed_keystore "Kurt Mercer" password user
create_signed_keystore "Tommy Zoran" password user
create_signed_keystore "Batman Hultin" password user
create_signed_keystore "Donny Nilsson" password user

create_signed_keystore "Mr Black" password agency

# Move the created certs to certs directory.
rm -rf bin/stores
mkdir bin/stores

cp stores/* bin/stores

