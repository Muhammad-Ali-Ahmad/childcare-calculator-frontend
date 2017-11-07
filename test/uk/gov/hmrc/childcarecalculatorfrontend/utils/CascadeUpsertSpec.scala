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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AreYouSelfEmployedOrApprenticeId, PartnerMaximumEarningsId, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class CascadeUpsertSpec extends SpecBase {

  "using the apply method for a key that has no special function" when {
    "the key doesn't already exists" must {
      "add the key to the cache map" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("value"))
      }
    }

    "data already exists for that key" must {
      "replace the value held against the key" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> JsString("original value")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("new value"))
      }
    }

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "northernIreland", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("northernIreland"))
      }
    }

    "saving a location other than northernIreland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "england", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("england" +
            "")
        )
      }
    }

    "saving the doYouLiveWithPartner" must {
      "remove an existing paid employment, partners adjusted tax code and who is in paid employment, you or partner get benefits, " +
        "vouchers, partners age, parent and partner self employed or apprentice, partner's max and min earnings when doYouLiveWithPartner is No" in {

        val originalCacheMap = new CacheMap("id", Map(PaidEmploymentId.toString -> JsBoolean(true),
          WhoIsInPaidEmploymentId.toString -> JsString(you), PartnerWorkHoursId.toString -> JsString("12"),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), PartnerChildcareVouchersId.toString -> JsString("yes"),
          DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
          YourPartnersAgeId.toString -> JsString("under18"),
          YourPartnersAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(true),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true), EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))
      }

      "remove an existing paid employment, who is in paid employment when doYouLiveWithpartner is Yes" in {
        val originalCacheMap = new CacheMap("id", Map(AreYouInPaidWorkId.toString -> JsBoolean(true),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))
      }
    }

    "saving the areYouInPaidWork" must {
      "remove an existing parent work hours, parents adjusted tax code, your childcare vouchers, do you get benefits, " +
        "your age, your min and max earnings when are you in paid work is no" in {
        val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
          HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), YourChildcareVouchersId.toString -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"),
          YourMinimumEarningsId.toString -> JsBoolean(true), YourMaximumEarningsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(AreYouInPaidWorkId.toString, false, originalCacheMap)
        result.data mustBe Map(AreYouInPaidWorkId.toString -> JsBoolean(false))
      }
    }

    "saving the are you your partner, or both of you in paid work" must {
      "remove an existing who's in paid work, parent work hours, partner work hours, parents adjusted tax code, partners adjusted tax code," +
        "either child care vouchers, who gets childcare vouchers, your childcare vouchers, partner childcare couchers, do you get benefits, " +
        "your age, partners age when paid employment is no" in {

        val originalCacheMap = new CacheMap("id", Map(WhoIsInPaidEmploymentId.toString -> JsString("both"), ParentWorkHoursId.toString -> JsString("12"),
          PartnerWorkHoursId.toString -> JsString("12"), HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true),
          DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("both"), YourChildcareVouchersId.toString -> JsString("yes"), PartnerChildcareVouchersId.toString -> JsString("yes"),
          DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"), YourPartnersAgeId.toString -> JsString("under18"),
          YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(true),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          YourMaximumEarningsId.toString -> JsBoolean(true), PartnerMaximumEarningsId.toString -> JsBoolean(true),
          EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PaidEmploymentId.toString, false, originalCacheMap)
        result.data mustBe Map(PaidEmploymentId.toString -> JsBoolean(false))
      }
    }

    "saving the whoIsInPaidEmployment" must {
      "remove an existing partner work hours, partners adjusted tax code, partner min and max earnings when whoIsInPaidEmployment is you" in {

        val originalCacheMap = new CacheMap("id", Map(PartnerWorkHoursId.toString -> JsString("12"),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), YourPartnersAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true), EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you))
      }

      "remove an existing parent work hours, parent adjusted tax code, your min and max earnings when whoIsInPaidEmployment is partner" in {

        val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
          HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"), YourMinimumEarningsId.toString -> JsBoolean(true),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString (SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          YourMaximumEarningsId.toString -> JsBoolean(true), EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner))
      }

      "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
        val originalCacheMap = new CacheMap("id", Map(YourChildcareVouchersId.toString -> JsString("yes")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both))
      }
    }

    "saving has your tax code been adjusted" must {
      "remove an existing do you know your adjusted tax code and your tax code when has your tax code been adjusted is no" in {
        val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(HasYourTaxCodeBeenAdjustedId.toString, false, originalCacheMap)
        result.data mustBe Map(HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(false))
      }
    }

    "saving do you know your adjusted tax code" must {
      "remove an existing your tax code when do you know adjusted tax code is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhatIsYourTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouKnowYourAdjustedTaxCodeId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(false))
      }
    }

    "saving has your partner's tax code been adjusted" must {
      "remove an existing do you know your partner's adjusted tax code and your partner's tax code when has your partner's tax code been adjusted is no" in {
        val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(HasYourPartnersTaxCodeBeenAdjustedId.toString, false, originalCacheMap)
        result.data mustBe Map(HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(false))
      }
    }

    "saving do you know your partners adjusted tax code" must {
      "remove an existing your partners tax code when do you know your partners adjusted tax code is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouKnowYourPartnersAdjustedTaxCodeId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(false))
      }
    }

    "saving the either childcare vouchers" must {
      "remove an existing who gets vouchers when either childcare vouchers is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhoGetsVouchersId.toString -> JsString("you")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(EitherGetsVouchersId.toString, "no", originalCacheMap)
        result.data mustBe Map(EitherGetsVouchersId.toString -> JsString("no"))
      }
    }

    "saving the your or your partner benefits" must {
      "remove an existing who gets benefits when you or your partner benefits is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhoGetsBenefitsId.toString -> JsString("you")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouOrYourPartnerGetAnyBenefitsId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false))
      }
    }
  }

    "saving the your minimumEarnings" must {
      "remove your maximum earnings and either of you max earnings when your minimum earnings is no" in {
        val originalCacheMap = new CacheMap("id", Map(YourMaximumEarningsId.toString -> JsBoolean(false),
                                                      EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourMinimumEarningsId.toString, false, originalCacheMap)
        result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(false))
      }

      "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
        val originalCacheMap = new CacheMap("id", Map(AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true))) //TODO Add in self employed less than 12 months
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourMinimumEarningsId.toString, true, originalCacheMap)
        result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(true))
      }
    }

    "saving the your partners minimumEarnings" must {
      "remove partners and either of you maximum earnings when partners minimum earnings is no" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerMaximumEarningsId.toString -> JsBoolean(false),
                                                      EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerMinimumEarningsId.toString, false, originalCacheMap)
        result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(false))
      }

      "remove your partners self employed or apprentice and partners self employed less than 12 months when partners minimum earnings is yes" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true))) //TODO Add in self employed less than 12 months
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerMinimumEarningsId.toString, true, originalCacheMap)
        result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(true))
      }
    }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  "addRepeatedValue" when {
    "the key doesn't already exist" must {
      "add the key to the cache map and save the value in a sequence" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value")))
      }
    }

    "the key already exists" must {
      "add the new value to the existing sequence" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> Json.toJson(Seq("value"))))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value", "new value")))
      }
    }

  }

"Paid Pension CY" when {
  "Save  YouPaidPensionCY data " must {
    "remove howMuchYouPayPension page data when user selects no option" in {
      val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(YouPaidPensionCYId.toString, false, originalCacheMap)

      result.data mustBe Map(YouPaidPensionCYId.toString -> JsBoolean(false))
    }

    "return original cache map when user selects yes option" in {
      val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(YouPaidPensionCYId.toString, true, originalCacheMap)

      result.data mustBe Map(YouPaidPensionCYId.toString.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
    }

  }

  "Save PartnerPaidPensionCY data " must {
    "remove howMuchPartnerPayPension page data when user selects no option" in {
      val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerPaidPensionCYId.toString, false, originalCacheMap)

      result.data mustBe Map(PartnerPaidPensionCYId.toString -> JsBoolean(false))
    }

    "return original cache map when user selects yes option" in {
      val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerPaidPensionCYId.toString, true, originalCacheMap)

      result.data mustBe Map(PartnerPaidPensionCYId.toString.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)))
    }

  }

  "Save BothPaidPensionCY data " must {
    "remove WhoPaysIntoPension, howMuchYouPayPension, howMuchPartnerPayPension and howMuchBothPayPension pages data" +
      " when user selects no option" in {
      val originalCacheMap = new CacheMap("id", Map(
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        WhoPaysIntoPensionId.toString -> JsString(You)))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(BothPaidPensionCYId.toString, false, originalCacheMap)

      result.data mustBe Map(BothPaidPensionCYId.toString -> JsBoolean(false))
    }

    "return original cache map when user selects yes option" in {
      val originalCacheMap = new CacheMap("id", Map(
        WhoPaysIntoPensionId.toString -> JsString(You),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(BothPaidPensionCYId.toString, true, originalCacheMap)

      result.data mustBe Map(BothPaidPensionCYId.toString.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(You),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
    }

  }

  "Save WhoPaysIntoPension data " must {
    "remove HowMuchPartnerPayPension and HowMuchBothPayPension page data when user selects you option" in {
      val originalCacheMap = new CacheMap("id", Map(
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(WhoPaysIntoPensionId.toString, You, originalCacheMap)

      result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(You),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
    }

    "remove HowMuchYouPayPension and HowMuchBothPayPension page data when user selects partner option" in {
      val originalCacheMap = new CacheMap("id", Map(
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(WhoPaysIntoPensionId.toString, Partner, originalCacheMap)

      result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(Partner),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)))
    }

    "remove HowMuchPartnerPayPension and HowMuchYouPayPension page data when user selects both option" in {
      val originalCacheMap = new CacheMap("id", Map(
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20"))))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(WhoPaysIntoPensionId.toString, Both, originalCacheMap)

      result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(Both),
        HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20")))
    }

    "return original cache map when there is any invalid value for the input" in {
      val originalCacheMap = new CacheMap("id", Map(
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20"))))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(WhoPaysIntoPensionId.toString, "invalidvalue", originalCacheMap)

      result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString("invalidvalue"),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20")))
    }
  }

}

  "Paid Pensions PY" when {
    "Save YouPaidPensionPY data " must{
      "remove howMuchBothPayPensionPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(YouPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(YouPaidPensionPYId.toString.toString -> JsBoolean(true),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerPaidPensionPY data " must{
      "remove howMuchPartnerPayPensionPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionPYId.toString.toString -> JsBoolean(true),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothPaidPensionPY data " must{
      "remove whoPaidIntoPensionPY, howMuchBothPayPensionPY, howMuchPartnerPayPensionPY and howMuchBothPayPensionPY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          WhoPaidIntoPensionPYId.toString -> JsString(You)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothPaidPensionPYId.toString.toString -> JsBoolean(true),
          WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoPaidIntoPensionPY data " must{
      "remove  howMuchPartnerPayPensionPY and howMuchBothPayPensionPY page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoPaidIntoPensionPYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove howMuchPartnerPayPensionPY and howMuchBothPayPensionPY page data when user selects partner option"in{
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoPaidIntoPensionPYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(Partner),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove howMuchBothPayPensionPY and howMuchPartnerPayPensionPY page data when user selects both option"in{
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoPaidIntoPensionPYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(Both),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input"in{
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoPaidIntoPensionPYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString("invalidvalue"),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20")))
      }
    }

  }

  "Other Income PY" when {
    "Save YourOtherIncomeLY data " must{
      "remove yourOtherIncomeAmountPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeLYId.toString.toString -> JsBoolean(true),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyOtherIncomeLY data " must{
      "remove partnerOtherIncomeAmountPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeLYId.toString.toString -> JsBoolean(true),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothOtherIncomeLY data " must{
      "remove whoOtherIncomePY, yourOtherIncomeAmountPY, partnerOtherIncomeAmountPY and otherIncomeAmountPY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          WhoOtherIncomePYId.toString -> JsString(You)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeLYId.toString.toString -> JsBoolean(true),
          WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoOtherIncomePY data " must{
      "remove PartnerOtherIncomeAmountPY and OtherIncomeAmountPY page data when user selects you option"in{
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoOtherIncomePYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove YourOtherIncomeAmountPY and OtherIncomeAmountPY page data when user selects partner option"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoOtherIncomePYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(Partner),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove PartnerOtherIncomeAmountPY and YourOtherIncomeAmountPY page data when user selects both option"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoOtherIncomePYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(Both),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoOtherIncomePYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString("invalidvalue"),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20")))
      }
    }

  }

  "Other Income CY" when {
    "Save YourOtherIncomeThisYear data " must{
      "remove yourOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyOtherIncomeThisYear data " must{
      "remove partnerOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothOtherIncomeThisYear data " must{
      "remove whoGetsOtherIncomeCY, yourOtherIncomeAmountCY, partnerOtherIncomeAmountCY and otherIncomeAmountCY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          WhoGetsOtherIncomeCYId.toString -> JsString(You)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoGetsOtherIncomeCY data " must{
      "remove PartnerOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects you option"in{
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove YourOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects partner option"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(Partner),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove PartnerOtherIncomeAmountCY and YourOtherIncomeAmountCY page data when user selects both option"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(Both),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input"in{
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString("invalidvalue"),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20", "20")))
      }
    }

  }


  "Benefits PY" when {
    "Save YouAnyTheseBenefitsPY data " must{
      "remove YouBenefitsIncomePY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouAnyTheseBenefitsPYId.toString, false, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouAnyTheseBenefitsPYId.toString, true, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsPYId.toString.toString -> JsBoolean(true),
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyTheseBenefitsPY data " must{
      "remove PartnerBenefitsIncomePY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyTheseBenefitsPYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyTheseBenefitsPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyTheseBenefitsPYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyTheseBenefitsPYId.toString.toString -> JsBoolean(true),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothAnyTheseBenefitsPY data " must{
      "remove whosHadBenefitsPY, youBenefitsIncomePY, partnerBenefitsIncomePY and bothBenefitsIncomePY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          WhosHadBenefitsPYId.toString -> JsString(You)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothAnyTheseBenefitsPYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothAnyTheseBenefitsPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhosHadBenefitsPYId.toString -> JsString(You),
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothAnyTheseBenefitsPYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothAnyTheseBenefitsPYId.toString.toString -> JsBoolean(true),
          WhosHadBenefitsPYId.toString -> JsString(You),
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoHadBenefitsPY data " must{
      "remove partnerBenefitsIncomePY and bothBenefitsIncomePY page data when user selects you option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsPYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsPYId.toString -> JsString(You),
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove youBenefitsIncomePY and bothBenefitsIncomePY page data when user selects partner option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsPYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsPYId.toString -> JsString(Partner),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove partnerBenefitsIncomePY and youBenefitsIncomePY page data when user selects both option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsPYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsPYId.toString -> JsString(Both),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsPYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsPYId.toString -> JsString("invalidvalue"),
          YouBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomePYId.toString -> JsNumber(BigDecimal(20)),
          BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20", "20")))
      }
    }

  }

  "Benefits CY" when {
    "Save YouAnyTheseBenefitsCY data " must{
      "remove YouBenefitsIncomeCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouAnyTheseBenefitsIdCY.toString, false, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsIdCY.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YouAnyTheseBenefitsIdCY.toString, true, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsIdCY.toString.toString -> JsBoolean(true),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyTheseBenefitsCY data " must{
      "remove PartnerBenefitsIncomeCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyTheseBenefitsCYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyTheseBenefitsCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerAnyTheseBenefitsCYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyTheseBenefitsCYId.toString.toString -> JsBoolean(true),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothAnyTheseBenefitsCY data " must{
      "remove whosHadBenefits, youBenefitsIncomeCY, partnerBenefitsIncomeCY and BenefitsIncomeCY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          WhosHadBenefitsId.toString -> JsString(You)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothAnyTheseBenefitsCYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothAnyTheseBenefitsCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhosHadBenefitsId.toString -> JsString(You),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(BothAnyTheseBenefitsCYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothAnyTheseBenefitsCYId.toString.toString -> JsBoolean(true),
          WhosHadBenefitsId.toString -> JsString(You),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }


    "Save WhosHadBenefits CY data " must{
      "remove PartnerBenefitsIncomeCY and BenefitsIncomeCY page data when user selects you option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsId.toString, You, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsId.toString -> JsString(You),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove youBenefitsIncomeCY and BenefitsIncomeCY page data when user selects partner option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsId.toString -> JsString(Partner),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove PartnerBenefitsIncomeCY and youBenefitsIncomeCY page data when user selects both option"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsId.toString -> JsString(Both),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input"in{
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20"))))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhosHadBenefitsId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhosHadBenefitsId.toString -> JsString("invalidvalue"),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY("20", "20")))
      }
    }
  }

}
