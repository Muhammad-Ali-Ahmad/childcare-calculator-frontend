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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location

class ResultsViewModelSpec extends SpecBase {

  "ResultViewModel" must {
    "return correct number of eligible schemes" in {
      val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = None, esc = None, location = location, hasChildcareCosts = true)
      resultsView.noOfEligibleSchemes mustBe 2
    }

    "return number of eligible schemes 0 when there is no eligible scheme" in {
      val resultsView = ResultsViewModel(location = location, hasChildcareCosts = true)
      resultsView.noOfEligibleSchemes mustBe 0
    }
  }

  "isEligibleOnlyTo15FreeHours" must {
    "return true" when {
      "user is eligible only to 15 Free Hours and no other schems" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(15), esc = None, location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyTo15Hours mustBe true
      }
    }

    "return false" when {
      "user is not eligible to 15 Free Hours" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(30), esc = None, location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyTo15Hours mustBe false
      }
    }
  }

  "isEligibleForAllButVouchers" must {
   "return true" when {
     "user is eligible for all the schemes but vouchers" in {
       val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = None, location = location, hasChildcareCosts = true)
       resultsView.isEligibleForAllButVouchers mustBe true
     }
   }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButVouchers mustBe false
      }
    }
  }

  "isEligibleForAllButTc" must {
    "return true" when {
      "user is eligible for all the schemes but tax credits" in {
        val resultsView = ResultsViewModel(tc = None, tfc = Some(100), freeHours = Some(200), esc = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButTc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButTc mustBe false
      }
    }
  }

  "isEligibleForAllButFreeHours" must {
    "return true" when {
      "user is eligible for all the schemes but free hours" in {
        val resultsView = ResultsViewModel(freeHours = None, tfc = Some(100), tc = Some(200), esc = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButFreeHours mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButTc mustBe false
      }
    }
  }

  "isEligibleForAllButTfc" must {
    "return true" when {
      "user is eligible for all the schemes but TFC" in {
        val resultsView = ResultsViewModel(tc = Some(300), tfc = None, freeHours = Some(200), esc = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButTfc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleForAllButTfc mustBe false
      }
    }
  }

  "isEligibleForFreeHoursAndTFC" must {
    "return true" when {
      "user is eligible for Free hours and TFC" in {
        val resultsView = ResultsViewModel(esc = None, tc = None, tfc = Some(500), freeHours = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndTfc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndTfc mustBe false
      }
    }
  }

  "isEligibleOnlyForFreeHoursAndTC" must {
    "return true" when {
      "user is eligible for Free hours and TC" in {
        val resultsView = ResultsViewModel(esc = None, tfc = None, tc = Some(500), freeHours = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndTc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndTc mustBe false
      }
    }
  }

  "isEligibleForFreeHoursAndESC" must {
    "return true" when {
      "user is eligible for Free hours and ESC" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, esc = Some(500), freeHours = Some(200), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndEsc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForFreeHoursAndEsc mustBe false
      }
    }
  }

  "isEligibleOnlyForTCAndTFC" must {
    "return true" when {
      "user is eligible only for TC and TFC" in {
        val resultsView = ResultsViewModel(tc = Some(500), tfc = Some(200), freeHours = None, esc = None, location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTCAndTfc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTCAndTfc mustBe false
      }
    }
  }

  "isEligibleOnlyForTCAndESC" must {
    "return true" when {
      "user is only eligible for TC and ESC" in {
        val resultsView = ResultsViewModel(tc = Some(500), esc = Some(300), tfc = None, freeHours = None, location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTCAndEsc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTCAndEsc mustBe false
      }
    }
  }

  "isEligibleOnlyForTFCAndESC" must {
    "return true" when {
      "user is eligible only for TFC and ESC" in {
        val resultsView = ResultsViewModel(esc = Some(500), tfc = Some(200), freeHours = None, tc = None, location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTfcAndEsc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300), location = location, hasChildcareCosts = true)
        resultsView.isEligibleOnlyForTfcAndEsc mustBe false
      }
    }
  }

  val location = Location.ENGLAND

}