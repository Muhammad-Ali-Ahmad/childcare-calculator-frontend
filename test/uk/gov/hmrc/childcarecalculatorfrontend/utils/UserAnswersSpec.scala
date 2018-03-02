/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.{over16,under16,over16WithBirthdayBefore31stOfAugust,over19}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswersSpec extends WordSpec with MustMatchers with OptionValues {

  "return partner when user lives with partner and the answer to whoIsInPaidEmployment returns 'partner'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("partner"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("partner")) mustEqual "partner"
  }

  "return both when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("both"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("both")) mustEqual "both"
  }

  "return you when the answer to whoIsInPaidEmployment returns 'you'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("you"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
  }

  "return you when user does not live with partner" in {
    val answers: CacheMap = cacheMap(
      DoYouLiveWithPartnerId.toString -> JsBoolean(false)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
  }

  ".childrenOver16" must {

    "return no children over 16" in {
      val under16 = if (LocalDate.now().getMonthOfYear < 8) LocalDate.now().minusYears(16).minusMonths(1) else LocalDate.now()

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", under16)),
          "1" -> Json.toJson(AboutYourChild("Baz", under16))
        )
      )

      val result = helper(answers).childrenOver16
      result.get.size mustBe 0
    }


    "return any children who are over 16" in {

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16)),
          "4" -> Json.toJson(AboutYourChild("Josh", over19))
        )
      )

      val result = helper(answers).childrenOver16
      result.value must contain(0 -> AboutYourChild("Foo", over16))
      result.value must contain(3 -> AboutYourChild("Baz", over16))
      result.value must contain(4 -> AboutYourChild("Josh", over19))
    }

    "return `None` when there are no children defined" in {
      val answers: CacheMap = cacheMap()
      helper(answers).childrenOver16 mustNot be(defined)
    }
  }

  ".extract16YearsOldWithBirthdayBefore31stAugust" must {
    "return the number of children of 16 years and dob before 31st August" in {

      val over19 = LocalDate.now.minusYears(19)
      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now().minusYears(16))),
          "1" -> Json.toJson(AboutYourChild("Bar", LocalDate.now().minusYears(16))),
          "2" -> Json.toJson(AboutYourChild("Quux", over19))
        )
      )

      val parametersMap = Map(
        0 -> AboutYourChild("Foo", LocalDate.now().minusYears(16)),
        1 -> AboutYourChild("Bar", LocalDate.now().minusYears(16)),
        2 -> AboutYourChild("Quux", over19))


      val result = helper(answers).extract16YearOldsWithBirthdayBefore31stAugust(Some(parametersMap))

      result.value must contain(0 -> AboutYourChild("Foo", LocalDate.now().minusYears(16)))
      result.value must contain(1 -> AboutYourChild("Bar", LocalDate.now().minusYears(16)))
    }
  }

  ".is16ThisYearAndDateOfBirthIsAfter31stAugust" must{
    "not return any children who are over 16 but Birthday is before 31st of August" in {
      val over16WithBirthdayBefore31stOfAugust = if (LocalDate.now().getMonthOfYear > 8) LocalDate.parse(s"${LocalDate.now.minusYears(16).getYear}-07-31") else LocalDate.now.minusYears(16)

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj("0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)))
      )
      val result = helper(answers).childrenOver16
      result.get.size mustBe 0
    }
  }

  ".childrenIdsForAgeBelow16" must {
    "return the seq of child ids who are less than 16 years old and exactly 16 whose dob is before 31st of august " in {

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))
        )
      )

      val result: Seq[Int] = helper(answers).childrenIdsForAgeExactly16
      result mustEqual Seq(3)
    }

    "return the empty sequence when children Map has None" in {
      val answers: CacheMap = cacheMap()
      val result: Seq[Int] = helper(answers).childrenIdsForAgeExactly16
      result mustEqual Seq()
    }
  }

    ".singleChildBelow16Yrs" must {
      "return true when the child aged exactly 16 and birthday before 31st of August are disabled" in {

        val answers: CacheMap = cacheMap(
          NoOfChildrenId.toString -> JsNumber(4),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over16)),
            "1" -> Json.toJson(AboutYourChild("Bar", under16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16)),
            "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 3)))

        val result: Boolean = helper(answers).singleChildBelow16Yrs
        result mustEqual true
      }

      "return false when the child aged exactly 16 and birthday before 31st of August are not  disabled" in {

        val answers: CacheMap = cacheMap(
          NoOfChildrenId.toString -> JsNumber(4),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over16)),
            "1" -> Json.toJson(AboutYourChild("Bar", under16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16)),
            "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)))

        val result: Boolean = helper(answers).singleChildBelow16Yrs
        result mustEqual false
      }

      "return false when the child aged exactly 16 and birthday before 31st of August are not registered blind" in {

        val answers: CacheMap = cacheMap(
          NoOfChildrenId.toString -> JsNumber(4),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over16)),
            "1" -> Json.toJson(AboutYourChild("Bar", under16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16)),
            "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)))

        val result: Boolean = helper(answers).singleChildBelow16Yrs
        result mustEqual false
      }

      "return true when the child aged exactly 16 and birthday before 31st of August are not registered blind" in {

        val answers: CacheMap = cacheMap(
          NoOfChildrenId.toString -> JsNumber(4),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over16)),
            "1" -> Json.toJson(AboutYourChild("Bar", under16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16)),
            "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2,3)))

        val result: Boolean = helper(answers).singleChildBelow16Yrs
        result mustEqual true
      }
    }


  ".multipleChildrenBelow16Yrs" must {
    "return true when the children aged exactly 16 and birthday before 31st of August are disabled" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),

        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,2,3)))

      val result: Boolean = helper(answers).multipleChildrenBelow16Yrs
      result mustEqual true
    }

    "return true when the children aged exactly 16 and birthday before 31st of August are blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),

        WhichChildrenBlindId.toString -> Json.toJson(Seq(0,2,3)))

      val result: Boolean = helper(answers).multipleChildrenBelow16Yrs
      result mustEqual true
    }

    "return false when the children aged exactly 16 and birthday before 31st of August are not blind nor disabled" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(1,2)),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(2)))

      val result: Boolean = helper(answers).multipleChildrenBelow16Yrs
      result mustEqual false
    }
  }

".childrenIdsForAgeExactly16AndDisabled" must {
  "returns list with children exactly 16 years with dob before august and blind" in {

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(4),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
        "1" -> Json.toJson(AboutYourChild("Bar", under16)),
        "2" -> Json.toJson(AboutYourChild("Quux", under16)),
        "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
      WhichChildrenDisabilityId.toString -> Json.toJson(Seq(1,2)),
      WhichChildrenBlindId.toString -> Json.toJson(Seq(0,2,1,3)))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq(0,3)
  }

  "returns list with children exactly 16 years with dob before august and disable " in {

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(4),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
        "1" -> Json.toJson(AboutYourChild("Bar", under16)),
        "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
        "3" -> Json.toJson(AboutYourChild("Baz", under16))),
      WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,2,3)),
      WhichChildrenBlindId.toString -> Json.toJson(Seq(1,3)))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq(0,2)
  }

  "returns empty list with children exactly 16 years with dob before august and not disable " in {
    val over16 = if (LocalDate.now().getMonthOfYear < 8) LocalDate.now.minusYears(17) else LocalDate.now.minusYears(16)
    val under16 = LocalDate.now
    val over16WithBirthdayBefore31stOfAugust = if (LocalDate.now().getMonthOfYear > 8) {
      LocalDate.parse(s"${LocalDate.now.minusYears(16).getYear}-07-31")
    } else {
      LocalDate.now.minusYears(16)
    }

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(4),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
        "1" -> Json.toJson(AboutYourChild("Bar", under16)),
        "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
        "3" -> Json.toJson(AboutYourChild("Baz", under16))))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq()
  }

  "returns list with single child exactly 16 years with dob before august and disabled" in {

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(1),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust))),
      ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
      RegisteredBlindId.toString -> JsBoolean(false))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq(0)
  }

  "returns list with single child exactly 16 years with dob before august and blind" in {

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(1),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust))),
      ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
      RegisteredBlindId.toString -> JsBoolean(true))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq(0)
  }

  "returns empty list for single child exactly 16 years with dob before august and not blind or disabled" in {

    val answers: CacheMap = cacheMap(
      NoOfChildrenId.toString -> JsNumber(1),
      AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust))),
      ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
      RegisteredBlindId.toString -> JsBoolean(false))

    val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
    result mustEqual Seq()
  }

}

  ".childrenBelow16AndExactly16Disabled" when {
    "return the list of children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
          "3" -> Json.toJson(AboutYourChild("Baz", under16))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,3)))

      val result: List[Int] = helper(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq(0,1,3)
    }

    "return empty list when children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16WithBirthdayBefore31stOfAugust)),
          "1" -> Json.toJson(AboutYourChild("Bar", over16)),
          "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16))),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(1,3)))

      val result: List[Int] = helper(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq()
    }
  }

  ".childrenBelow16" must{
    "returns list of children id's whose age is less than 16" in{
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over19)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
          "3" -> Json.toJson(AboutYourChild("Baz", under16))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,3)))

      val result: List[Int] = helper(answers).childrenBelow16
      result mustEqual Seq(1,3)
    }

    "returns empty list   when chidren are over or exactly 16" in{
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over19)),
          "1" -> Json.toJson(AboutYourChild("Bar", over19)),
          "2" -> Json.toJson(AboutYourChild("Quux", over16WithBirthdayBefore31stOfAugust)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16WithBirthdayBefore31stOfAugust))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,3)))

      val result: List[Int] = helper(answers).childrenBelow16
      result mustEqual Seq()
    }
  }

  ".childrenWithDisabilityBenefits" must {

    "return `Some` if `whichChildrenDisability` is defined" in {
      val answers = helper(cacheMap(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0, 2)
    }

    "return `Some` if there is a single child with disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `Some(Set())` if there are multiple children without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `None` if `noOfChildren` and `whichChildrenDisability` are both undefined" in {
      val answers = helper(cacheMap(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }

    "return `None` if there is a single child and `childrenDisabilityBenefits` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }
  }

  ".childrenWithCosts" must {

    "return `Some` if there are multiple children and `whoHasChildcareCosts` is defined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2),
        WhoHasChildcareCostsId.toString -> Json.toJson(Seq(JsNumber(0)))
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `yes`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("yes")
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `not yet`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("notYet")
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child and `childcareCosts` is `no`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("no")
      ))
      answers.childrenWithCosts.value mustEqual Set.empty
    }

    "return `None` if there is a single child and `childcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1)
      ))
      answers.childrenWithCosts mustNot be(defined)
    }

    "return `None` if there are multiple children and `whoHasChildcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2)
      ))
      answers.childrenWithCosts mustNot be(defined)
    }
  }

  ".hasApprovedCosts" must {

    import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}

    val yesNoNotYetPositive: Seq[String] = Seq(YesNoNotYetEnum.YES.toString, YesNoNotYetEnum.NOTYET.toString)
    val yesNoUnsurePositive: Seq[String] = Seq(YesNoUnsureEnum.YES.toString, YesNoUnsureEnum.NOTSURE.toString)

    for(costs <- yesNoNotYetPositive; provider <- yesNoUnsurePositive) {
      s"return `true` if user has costs: $costs, and approved costs: $provider" in {
        val answers = helper(cacheMap(
          ChildcareCostsId.toString -> JsString(costs),
          ApprovedProviderId.toString -> JsString(provider)
        ))
        answers.hasApprovedCosts.value mustEqual true
      }
    }

    "return `false` if a user has no costs" in {
      val answers = helper(cacheMap(
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString)
      ))
      answers.hasApprovedCosts.value mustEqual false
    }

    yesNoNotYetPositive.foreach {
      costs =>
        s"return `false` if a user has costs: $costs, but they aren't approved" in {
          val answers = helper(cacheMap(
            ChildcareCostsId.toString -> JsString(costs),
            ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NO.toString)
          ))
        }
    }

    "return `None` if a user has costs but `approvedProvider` is undefined" in {
      val answers = helper(cacheMap(
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      ))
      answers.hasApprovedCosts mustNot be(defined)
    }

    "return `None` if a user `childcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.YES.toString)
      ))
      answers.hasApprovedCosts mustNot be(defined)
    }
  }

  def cacheMap(answers: (String, JsValue)*): CacheMap =
    CacheMap("", Map(answers: _*))

  def helper(map: CacheMap = cacheMap()): UserAnswers =
    new UserAnswers(map)
}
