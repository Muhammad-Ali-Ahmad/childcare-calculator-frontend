#!/bin/bash

echo "Applying migration HowMuchBothPayPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchBothPayPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchBothPayPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchBothPayPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchBothPayPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchBothPayPension.title = howMuchBothPayPension" >> ../conf/messages.en
echo "howMuchBothPayPension.heading = howMuchBothPayPension" >> ../conf/messages.en
echo "howMuchBothPayPension.field1 = Field 1" >> ../conf/messages.en
echo "howMuchBothPayPension.field2 = Field 2" >> ../conf/messages.en
echo "howMuchBothPayPension.checkYourAnswersLabel = howMuchBothPayPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchBothPayPension: Option[HowMuchBothPayPension] = cacheMap.getEntry[HowMuchBothPayPension](HowMuchBothPayPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchBothPayPension: Option[AnswerRow] = userAnswers.howMuchBothPayPension map {";\
     print "    x => AnswerRow(\"howMuchBothPayPension.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.HowMuchBothPayPensionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchBothPayPension complete"
