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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourResults

class AboutYourResultsViewSpec extends ViewBehaviours {

  def createView = () => aboutYourResults(frontendAppConfig, ResultsViewModel())(fakeRequest, messages)

  "AboutYourResults view" must {

    behave like normalPage(createView, "aboutYourResults")

    "contain back to results link" in {

      val doc = asDocument(createView())

      doc.getElementById("returnToResults").text() mustBe messages("aboutYourResults.return.link")
      doc.getElementById("returnToResults").attr("href") mustBe ResultController.onPageLoad().url
    }

    "display the correct title" in {

      val doc = asDocument(createView())
      assertContainsMessages(doc, messages("aboutYourResults.about.the.schemes"))

    }

    "display free hours content" when {
      "user is eligible for free hours display title" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.title"))
      }

      "user is eligible for free hours display first paragraph" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para1"))
      }

      "user is eligible for free hours display second paragraph" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para2"))
      }
    }

    "display TC content" when {
      "user is eligible for TC display title" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.title"))
      }

      "user is eligible for TC display first paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para1"))
      }

      "user is eligible for TC display second paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para2"))
      }

      "user is eligible for TC display third paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para3"))
      }
    }
  }
}
