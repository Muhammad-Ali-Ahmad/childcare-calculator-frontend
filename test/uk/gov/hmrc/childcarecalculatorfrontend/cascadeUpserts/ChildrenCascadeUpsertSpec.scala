/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts


import org.joda.time.LocalDate
import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class ChildrenCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {
  val over19 = LocalDate.now.minusYears(19).minusDays(1)
  val over16 = LocalDate.now.minusYears(16).minusDays(1)
  val exact15 = LocalDate.now.minusYears(15).plusMonths(1)
  val under16 = LocalDate.now

  val childStartEducationDate = new LocalDate(2017, 2, 1)

  lazy val disabilityBenefits: String = DisabilityBenefits.DISABILITY_BENEFITS.toString
  lazy val higherRateDisabilityBenefits: String = DisabilityBenefits.HIGHER_DISABILITY_BENEFITS.toString

  lazy val weekly: String = ChildcarePayFrequency.WEEKLY.toString
  lazy val monthly: String = ChildcarePayFrequency.MONTHLY.toString


  "Children Journey" when {
    "Save noOfChildren data " must {
      "remove relevant data in child journey when noOfChildren value is changed" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsString("3"),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
            "3" -> Json.toJson(AboutYourChild("Baz", under16)),
            "4" -> Json.toJson(AboutYourChild("Baz", under16))),
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "1" -> true
          ),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          ChildRegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          ChildRegisteredBlindId.toString -> JsBoolean(true),

          WhichBenefitsYouGetId.toString -> Json.toJson(Seq(3, 4)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        ))

        val result = cascadeUpsert(NoOfChildrenId.toString, "4", originalCacheMap)

        result.data mustBe Map(NoOfChildrenId.toString -> JsString("4"))
      }
    }
  }

}
