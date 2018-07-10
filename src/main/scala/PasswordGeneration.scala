import java.math.BigInteger
import java.security.spec.InvalidKeySpecException
import java.security.{NoSuchAlgorithmException, SecureRandom}

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException

import scala.util.{Failure, Success, Try}

object PasswordGeneration extends App {

  val SALT_BYTES        = 24
  val HASH_BYTES        = 24
  val PBKDF2_ITERATIONS = 1000

  val ITERATION_INDEX  = 0
  val SALT_INDEX       = 1
  val PBKDF2_INDEX     = 2
  val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
  val en               = createHash("p\r\nassw0Rd!").get
  println(en)
  println(validatePassword("p\r\nassw0Rd!", en))

  /**
   * Returns a salted PBKDF2 hash of the password.
   *
   * @param   password the password to hash
   * @return a salted PBKDF2 hash of the password
   */
  @throws[NoSuchAlgorithmException]
  @throws[InvalidKeySpecException]
  def createHash(password: String): Try[String] =
    createHash(password.toCharArray)

  /**
   * Returns a salted PBKDF2 hash of the password.
   *
   * @param   password the password to hash
   * @return a salted PBKDF2 hash of the password
   */
  @throws[NoSuchAlgorithmException]
  @throws[InvalidKeySpecException]
  def createHash(password: Array[Char]): Try[String] = {
    // Generate a random salt
    val random = new SecureRandom()
    val salt   = new Array[Byte](SALT_BYTES)
    random.nextBytes(salt)
    // Hash the password
    pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTES).map { value =>
      // format iterations:salt:hash
      PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(value)
    }
  }

  /**
   * Computes the PBKDF2 hash of a password.
   *
   * @param   password   the password to hash.
   * @param   salt       the salt
   * @param   iterations the iteration count (slowness factor)
   * @param   bytes      the length of the hash to compute in bytes
   * @return the PBDKF2 hash of the password
   */
  @throws[NoSuchAlgorithmException]
  @throws[InvalidKeySpecException]
  private def pbkdf2(
                      password: Array[Char],
                      salt: Array[Byte],
                      iterations: Int,
                      bytes: Int
  ): Try[Array[Byte]] = Try {
    val skf  = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
    val spec = new PBEKeySpec(password, salt, iterations, bytes * 8)
    skf.generateSecret(spec).getEncoded
  }

  private def toHex(array: Array[Byte]): String = {
    val bi            = new BigInteger(1, array)
    val hex           = bi.toString(16)
    val paddingLength = (array.length * 2) - hex.length
    if (paddingLength > 0)
      String.format("%0" + paddingLength + "d", 0: java.lang.Integer) + hex
    else hex
  }

  /**
   * Converts a string of hexadecimal characters into a byte array.
   *
   * @param   hex the hex string
   * @return the hex string decoded into a byte array
   */
  private def fromHex(hex: String) = {
    val binary = new Array[Byte](hex.length / 2)
    var i      = 0
    while (i < binary.length) {
      binary(i) = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte
      i += 1
    }
    binary
  }

  /**
   * Compares two byte arrays in length-constant time. This comparison method
   * is used so that password hashes cannot be extracted from an on-line
   * system using a timing attack and then attacked off-line.
   *
   * @param   a the first byte array
   * @param   b the second byte array
   * @return true if both byte arrays are the same, false if not
   */
  private def slowEquals(a: Array[Byte], b: Array[Byte]): Boolean = {
    var diff = a.length ^ b.length
    var i    = 0
    while (i < a.length && i < b.length) {
      diff |= a(i) ^ b(i)
      i += 1
    }
    diff == 0
  }

  /**
   * Validates a password using a hash.
   *
   * @param   password the password to check
   * @param   goodHash the hash of the valid password
   * @return true if the password is correct, false if not
   */
  def validatePassword(password: String, goodHash: String): Boolean =
    validatePassword(password.toCharArray, goodHash)

  /**
   * Validates a password using a hash.
   * Hash has valid format which is separated by colon(:)
   *
   * @param   password    the password to check
   * @param   hashedValue the hash of the valid password
   * @return true if the password is correct, false if not
   */
  @throws[NoSuchAlgorithmException]
  @throws[InvalidKeySpecException]
  def validatePassword(password: Array[Char], hashedValue: String): Boolean =
    try {
      // Decode the hash into its parameters
      val params     = hashedValue.split(":")
      val iterations = params(ITERATION_INDEX).toInt
      val salt       = fromHex(params(SALT_INDEX))
      val hash       = fromHex(params(PBKDF2_INDEX))
      // Compute the hash of the provided password, using the same salt,
      // iteration count, and hash length
      pbkdf2(password, salt, iterations, hash.length) match {
        case Success(hashed) => slowEquals(hash, hashed)
        case Failure(_)      => false
      }
    } catch {
      case _: Exception => false
    }
}
