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

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, CheckMode}
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.{Section, AnswerRow, AnswerSection}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResult

class FreeHoursResultViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "freeHoursResult"

  def createView = () => freeHoursResult(frontendAppConfig, "", Seq())(fakeRequest, messages)

  def createViewWithAnswers = (location: String,
                                answerSections: Seq[Section]) => freeHoursResult(frontendAppConfig, location, answerSections)(fakeRequest, messages)

  "FreeHoursResult view" must {

    behave like normalPage(createView,
      messageKeyPrefix,
      "notEligibleInfo",
      "info.esc",
      "info.tfc",
      "info.tc",
      "notEligible.heading",
      "toBeEligible.heading",
      "toBeEligible.info1",
      "toBeEligible.info2",
      "summary.heading",
      "summary.info")

    behave like pageWithBackLink(createView)

  }

  "FreeHoursResult view" when {
    "rendered" must {
      "contain 15 free hours and correct not eligibility guidance for location England" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel","england", true, routes.LocationController.onPageLoad(NormalMode).url)
        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers("england", answerSections))
        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.england"))
        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info"))

      }

      "contain 16 free hours and correct not eligibility for location Scotland" in {

        val doc = asDocument(createViewWithAnswers("scotland", Seq()))
        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info"))

      }

      "contain 10 free hours and correct not eligibility for location Wales" in {
        val doc = asDocument(createViewWithAnswers("wales", Seq()))
        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info"))
      }

      "contain 12.5 free hours and correct not eligibility for location NI" in {
        val doc = asDocument(createViewWithAnswers("northern-ireland", Seq()))
        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info.northern-ireland"))
      }

     "display all the answer rows with correct contents " in {
       val answerSections = Seq(AnswerSection(None, Seq(
         AnswerRow("childAgedTwo.checkYourAnswersLabel",
           "site.no",
           true,
           routes.ChildAgedTwoController.onPageLoad(NormalMode).url),
         AnswerRow("childAgedThreeOrFour.checkYourAnswersLabel",
           "site.no",
           true,
           routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode).url),
         AnswerRow("expectChildcareCosts.checkYourAnswersLabel",
           "expectChildcareCosts.yes",
           true,
           routes.ExpectChildcareCostsController.onPageLoad(NormalMode).url)
       )))

       val doc = asDocument(createViewWithAnswers("england", answerSections))

       assertContainsText(doc, messagesApi("childAgedTwo.checkYourAnswersLabel"))
       assertContainsText(doc, messagesApi("site.no"))
       assertContainsText(doc, messagesApi(messages("site.edit")))
       assertContainsText(doc, routes.ChildAgedTwoController.onPageLoad(NormalMode).url)

       assertContainsText(doc, messagesApi("childAgedThreeOrFour.checkYourAnswersLabel"))
       assertContainsText(doc, routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode).url)

       assertContainsText(doc, messagesApi("expectChildcareCosts.checkYourAnswersLabel"))
       assertContainsText(doc, routes.ExpectChildcareCostsController.onPageLoad(NormalMode).url)
     }

    }

  }

}
