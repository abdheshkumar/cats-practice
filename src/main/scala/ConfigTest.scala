import scala.util.Try
object ConfigTest extends App {
  import PureConfigUtils._
  import cats.implicits._
  import com.typesafe.config.{Config, ConfigObject, ConfigValueFactory, _}
  import pureconfig.error.{ConvertFailure, _}
  import pureconfig.{loadConfig, ConfigReader, EnumCoproductHint}
  import pureconfig.FieldCoproductHint
  import scala.collection.JavaConverters._
  import scala.reflect.{ClassTag, _}

  sealed abstract class MeansType(val value: String)

  object MeansType {
    implicit val meansTypeHint: EnumCoproductHint[MeansType] = new EnumCoproductHint[MeansType]
    case object Consent           extends MeansType(value = "consent")
    case object Authentication    extends MeansType(value = "authentication")
    case object AppAuthentication extends MeansType(value = "app-authentication")
  }

  sealed abstract class PartyType(val value: String)

  object PartyType {

    implicit val partyTypeHint: EnumCoproductHint[PartyType] = new EnumCoproductHint[PartyType]
    case object Customer         extends PartyType(value = "customer")
    case object Worker           extends PartyType(value = "worker")
    case object AssistedCustomer extends PartyType(value = "assisted-customer")
  }

  sealed abstract class Platform(value: String)

  object Platform {

    implicit val platformHint: EnumCoproductHint[Platform] = new EnumCoproductHint[Platform]

    case object Web    extends Platform(value = "web")
    case object Mobile extends Platform(value = "mobile")

  }

  sealed abstract class Strength(val value: String)

  object Strength {

    implicit val platformHint: EnumCoproductHint[Strength] = new EnumCoproductHint[Strength]
    case object Basic  extends Strength("basic")
    case object Strong extends Strength("strong")
  }

  sealed abstract class MeansAgreementType(val name: String)

  object MeansAgreementType {

    implicit val meansAgreementTypeHint: EnumCoproductHint[MeansAgreementType] =
      new EnumCoproductHint[MeansAgreementType]

    case object DE_MEANS_G2P_EMAIL extends MeansAgreementType("DE_MEANS_G2P_EMAILa")
    case object ING_MEANS_SRP      extends MeansAgreementType("ING_MEANS_SRP")
    case object WB_MEANS_MTOKEN    extends MeansAgreementType("WB_MEANS_MTOKEN")
    case object RO_MEANS_OTP       extends MeansAgreementType("RO_MEANS_OTP")
    case object CZ_MEANS_3OUT6PIN  extends MeansAgreementType("CZ_MEANS_3OUT6PIN")
    case object RRO_MEANS_MTAN     extends MeansAgreementType("RO_MEANS_MTAN")
    case object WB_MEANS_MTOKEN_ID extends MeansAgreementType("WB_MEANS_MTOKEN_ID")
    case object NL_OTP             extends MeansAgreementType("NL_OTP")
    case object BE_MEANS_CAP       extends MeansAgreementType("BE_MEANS_CAP")
    case object PH_CIF_PIN         extends MeansAgreementType("PH_CIF_PIN")
    case object PH_CERTIFICATE_FINGERPRINT_PIN
        extends MeansAgreementType("PH_CERTIFICATEFINGERPRINT_PIN")
    case object ING_MEANS_EMAIL_PW       extends MeansAgreementType("ING_MEANS_EMAIL_PW")
    case object EXTERNAL_ID_MEANS        extends MeansAgreementType("EXTERNAL_ID_MEANS")
    case object NL_OTP_PRVT              extends MeansAgreementType("NL_OTP_PRVT")
    case object WB_MEANS_IDENTIFICATION  extends MeansAgreementType("WB_MEANS_IDENTIFICATION")
    case object WB_MEANS_ONETIMEPASSWORD extends MeansAgreementType("WB_MEANS_ONETIMEPASSWORD")
    case object AT_MEANS_UNPW            extends MeansAgreementType("AT_MEANS_UNPW")
    case object DE_MEANS_IBPIN           extends MeansAgreementType("DE_MEANS_IBPIN")
    case object DE_MEANS_ITAN            extends MeansAgreementType("DE_MEANS_ITAN")
    case object DE_MEANS_MTAN            extends MeansAgreementType("DE_MEANS_MTAN")
    case object DE_MEANS_DIBAKEY         extends MeansAgreementType("DE_MEANS_DIBAKEY")
    case object DE_MEANS_APP             extends MeansAgreementType("DE_MEANS_APP")
    case object DE_MEANS_SCR_QSTN        extends MeansAgreementType("DE_MEANS_SCR_QSTN")
    case object CZ_MEANS_ID              extends MeansAgreementType("CZ_MEANS_ID")
    case object ES_MEANS_ID              extends MeansAgreementType("ES_MEANS_ID")
    case object ING_MEANS_SCD_FCTR       extends MeansAgreementType("ING_MEANS_SCD_FCTR")
    case object ING_MEANS_CRONTO_HW_TOKN extends MeansAgreementType("ING_MEANS_CRONTO_HW_TOKN")
    case object VAM_MEANS_PSWD_ID        extends MeansAgreementType("VAM_MEANS_PSWD_ID")
  }

  case class MeansClassification(factor: String, strength: String, `type`: String)

  sealed trait Component {
    def platform: String
    def interactive: Boolean
    def componentName: Option[String]
  }

  object Component {

    implicit val animalConfHint: FieldCoproductHint[Component] =
      new FieldCoproductHint[Component]("platform")

    case class Mobile(
                        platform: String = "mobile",
                        interactive: Boolean,
                        componentName: Option[String]
    ) extends Component

    case class Web(platform: String = "web", interactive: Boolean, componentName: Option[String])
        extends Component

  }
  case class MeansDetail(
                      name: String,
                      `type`: List[MeansType],
                      partyType: List[PartyType],
                      description: String,
                      classification: List[MeansClassification],
                      frontends: Option[List[Component]],
                      applicationName: String,
                      team: String,
                      enabled: Boolean,
                      identifying: Boolean,
                      meansAgreementType: Option[MeansAgreementType]
  )
  case class AuthenticationPolicies(levelOfAssurance: Int, cost: Int, means: List[MeansDetail])

  case class ParseMeansDetail(means: MeansDetail)
  object ParseMeansDetail {
    type MeansIdentifier = String
    val rootPath = "means"
    //This implicit will called when we load means details only and previously it was causing StackOverFlow issue, now we don't have to increase -xss2M
    implicit val meansDetailConfigReader: ConfigReader[ParseMeansDetail] =
      ConfigReader.fromFunction {
        case configObject: ConfigObject =>
          configObject.asScala.headOption match {
            case Some((key, configObject: ConfigObject)) =>
              loadConfig[MeansDetail](configObject.toConfig)
                .flatMap(validateMeansDetail(key, _))
                .map(ParseMeansDetail.apply)
            case Some((key, other)) =>
              logConfigFailure(s"$rootPath.$key", other, classOf[ConfigObject].getName)
            case None =>
              logConfigFailure(
                rootPath,
                ConfigValueFactory.fromAnyRef(
                  s"${classOf[ConfigObject].getName} must have an element"
                ),
                classOf[ConfigObject].getName
              )
          }
        case other =>
          logConfigFailure(rootPath, other, classOf[ConfigObject].getName)
      }

    private def validateMeansDetail(
                        key: String,
                        meansDetail: MeansDetail
    ): Either[ConfigReaderFailures, MeansDetail] =
      if (key == meansDetail.name) Right(meansDetail)
      else
        logConfigFailure(
          s"$rootPath.$key",
          ConfigValueFactory.fromAnyRef(
            s"key: $key is not matched with means name: ${meansDetail.name}"
          ),
          classOf[ConfigObject].getName
        )

    def load(config: Config): Either[ConfigReaderFailures, List[MeansDetail]] =
      readConfigObject[ParseMeansDetail](config.getObject("means")).map(_.map(_.means))
  }

  object PureConfigUtils {

    def readConfigObject[A: ConfigReader](
                        config: ConfigObject
    )(implicit ct: ClassTag[A]): Either[ConfigReaderFailures, List[A]] = {
      type G[T] = Either[ConfigReaderFailures, T]
      config.asScala
        .map {
          case (key, configObject) =>
            loadConfig[A](ConfigValueFactory.fromMap(Map(key -> configObject).asJava).toConfig)
        }
        .toList
        .sequence[G, A]
    }

    def logConfigFailure[A: ClassTag](
                        path: String,
                        configValue: ConfigValue,
                        reqType: String
    ): Either[ConfigReaderFailures, A] = {
      val failure: ConvertFailure = ConvertFailure(
        CannotConvert(
          configValue.toString,
          classTag[A].runtimeClass.getName,
          s"It require $reqType"
        ),
        ConfigValueLocation(configValue.origin()),
        path
      )
      Left(ConfigReaderFailures(failure))
    }
  }

  case class ParseAuthenticationPolicies(auth: List[AuthenticationPolicies])
  object ParseAuthenticationPolicies {
    type LevelOfAssurance = Int
    type G[T]             = Either[ConfigReaderFailures, T]
    val rootPath = "policies"
    private case class InnerPolicies(cost: Int, means: List[MeansDetail])

    //This implicit will called when we load AuthenticationPolicies and it internally call macro generated implicit of MeansDetails instead of ParseMeansDetails.meansDetailConfigReader
    implicit val authenticationPoliciesConfigReader: ConfigReader[ParseAuthenticationPolicies] =
      ConfigReader.fromFunction {
        case config: ConfigObject =>
          config.asScala.headOption match {
            case Some((key, configList: ConfigList)) =>
              def buildError(ex: Throwable): ConfigReaderFailures =
                ConfigReaderFailures(
                  ConvertFailure(
                    CannotConvert(key, Int.getClass.getName, ex.getMessage),
                    ConfigValueLocation(configList.origin()),
                    rootPath
                  )
                )
              val intKey: G[Int] =
                Try(key.toInt).toEither.leftMap(ex => buildError(ex))

              val authenticationPolicies = configList.asScala.toList
                .collect {
                  case a: ConfigObject =>
                    val t: G[InnerPolicies] =
                      loadConfig[InnerPolicies](a.toConfig)
                    //use (intKey, t).mapN((k, in) => AuthenticationPolicies(k, in.cost, in.means)) if you are using cats
                    (intKey, t).mapN((k, in) => AuthenticationPolicies(k, in.cost, in.means))
                }
                .sequence[G, AuthenticationPolicies]
              authenticationPolicies.map(ParseAuthenticationPolicies.apply)
            case Some((key, other)) =>
              logConfigFailure(s"$rootPath.$key", other, classOf[ConfigList].getName)
            case None =>
              logConfigFailure(
                rootPath,
                ConfigValueFactory.fromAnyRef(s"${classOf[ConfigObject]} must have an element"),
                classOf[ConfigList].getName
              )
          }
        case other =>
          logConfigFailure(rootPath, other, classOf[ConfigList].getName)
      }

    def load(config: Config): Either[ConfigReaderFailures, List[AuthenticationPolicies]] = {
      val path = "policies.authentication-policies"
      val securityPolicies: Either[ConfigReaderFailures, List[AuthenticationPolicies]] =
        readConfigObject[ParseAuthenticationPolicies](
          config.getObject(path)
        ).map(_.flatMap(_.auth))

      securityPolicies match {
        case Right(secPolicies) => validatePolicies(secPolicies, path).map(_ => secPolicies)
        case a @ Left(_)        => a
      }
    }

    /**
     * Validate policies
     *
     * @param authenticationPolicies - list of Policies
     * @return - list of Policies
     */
    private def validatePolicies(
                        securityPolicies: List[AuthenticationPolicies],
                        path: String
    ): Either[ConfigReaderFailures, Unit] = {
      val listOfPolicies: List[List[String]]                 = securityPolicies.map { _.means.map { _.name } }
      val (singleFactorOfPolicies, mutlipleFactorOfPolicies) = listOfPolicies.partition(_.size < 2)
      val flattenSingleFactorOfPolicies                      = singleFactorOfPolicies.flatten

      if (mutlipleFactorOfPolicies
          .flatMap(a => a.init)
          .forall(flattenSingleFactorOfPolicies contains)) Right(())
      else
        logConfigFailure(
          path,
          ConfigValueFactory.fromAnyRef(
            s"first factor means are not present for policy $mutlipleFactorOfPolicies"
          ),
          path
        )
    }
  }

  println(ParseMeansDetail.load(ConfigFactory.load()))
  println("------------------------------------")
  println(ParseAuthenticationPolicies.load(ConfigFactory.load()))
}
