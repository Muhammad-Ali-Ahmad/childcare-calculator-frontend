#!/bin/bash

echo "Applying migration AreYouSelfEmployedOrApprentice"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /areYouSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.AreYouSelfEmployedOrApprenticeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /areYouSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.AreYouSelfEmployedOrApprenticeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAreYouSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.AreYouSelfEmployedOrApprenticeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAreYouSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.AreYouSelfEmployedOrApprenticeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "areYouSelfEmployedOrApprentice.title = areYouSelfEmployedOrApprentice" >> ../conf/messages.en
echo "areYouSelfEmployedOrApprentice.heading = areYouSelfEmployedOrApprentice" >> ../conf/messages.en
echo "areYouSelfEmployedOrApprentice.option1 = areYouSelfEmployedOrApprentice" Option 1 >> ../conf/messages.en
echo "areYouSelfEmployedOrApprentice.option2 = areYouSelfEmployedOrApprentice" Option 2 >> ../conf/messages.en
echo "areYouSelfEmployedOrApprentice.checkYourAnswersLabel = areYouSelfEmployedOrApprentice" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def areYouSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](AreYouSelfEmployedOrApprenticeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def areYouSelfEmployedOrApprentice: Option[AnswerRow] = userAnswers.areYouSelfEmployedOrApprentice map {";\
     print "    x => AnswerRow(\"areYouSelfEmployedOrApprentice.checkYourAnswersLabel\", s\"areYouSelfEmployedOrApprentice.$x\", true, routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouSelfEmployedOrApprentice complete"
