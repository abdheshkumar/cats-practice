package jwt

import java.util

import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.{ErrorCodes, InvalidJwtException, JwtConsumerBuilder}

import scala.util.{Failure, Success, Try}

object JQTApp extends App {
  val rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048)

  /**
    * Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
    */
  def generateJWTToken() = {
    // Give the JWK a Key ID (kid), which is just the polite thing to do
    rsaJsonWebKey.setKeyId("k1")
    // Create the Claims, which will be the content of the JWT
    val claims = new JwtClaims
    claims.setIssuer("Issuer") // who creates the token and signs it
    claims.setAudience("Audience") // to whom the token is intended to be sent
    claims.setExpirationTimeMinutesInTheFuture(10) // time when the token will expire (10 minutes from now)
    claims.setGeneratedJwtId() // a unique identifier for the token
    claims.setIssuedAtToNow() // when the token was issued/created (now)
    claims.setNotBeforeMinutesInThePast(2) // time before which the token is not yet valid (2 minutes ago)
    claims.setSubject("subject") // the subject/principal is whom the token is about
    claims.setClaim("email", "mail@example.com") // additional claims/attributes about the subject can be added
    val groups = util.Arrays.asList("group-one", "other-group", "group-three")
    claims.setStringListClaim("groups", groups) // multi-valued claims work too and will end up as a JSON array

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    val jws = new JsonWebSignature
    // The payload of the JWS is JSON content of the JWT Claims
    jws.setPayload(claims.toJson)
    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey)
    // Set the Key ID (kid) header because it's just the polite thing to do.
    // We only have one key in this example but a using a Key ID helps
    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId)
    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256)
    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
    // representation, which is a string consisting of three dot ('.') separated
    // base64url-encoded parts in the form Header.Payload.Signature
    // If you wanted to encrypt it, you can simply set this jwt as the payload
    // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
    val jwt = jws.getCompactSerialization
    // Now you can do something with the JWT. Like send it to some other party
    // over the clouds and through the interwebs.
    println("JWT: " + jwt)
    jwt
  }

  /**
    * Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will be used to validate and process the JWT.
    * The specific validation requirements for a JWT are context dependent, however,
    * it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
    * and audience that identifies your system as the intended recipient.
    * If the JWT is encrypted too, you need only provide a decryption key or decryption key resolver to the builder.
    *
    * @param jwt
    */
  def verifyJQToken(jwt: String) = {
    val jwtConsumer = new JwtConsumerBuilder()
      .setRequireExpirationTime() // the JWT must have an expiration time
      .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
      .setRequireSubject() // the JWT must have a subject claim
      .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
      .setExpectedAudience("Audience") // to whom the JWT is intended for
      .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
      .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
      new AlgorithmConstraints(ConstraintType.WHITELIST, // which is only RS256 here
        AlgorithmIdentifiers.RSA_USING_SHA256))
      .build(); // create the JwtConsumer instance
    Try(jwtConsumer.processToClaims(jwt)) match {
      case Success(jwtClaims) => println("JWT validation succeeded! " + jwtClaims)
      case Failure(ex: InvalidJwtException) =>
        println("Invalid JWT! " + ex)
        if (ex.hasExpired) println("JWT expired at " + ex.getJwtContext.getJwtClaims.getExpirationTime)
        if (ex.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) println("JWT had wrong audience: " + ex.getJwtContext.getJwtClaims.getAudience)
      case Failure(ex) => println(ex)
    }
  }

  verifyJQToken(generateJWTToken())


}
