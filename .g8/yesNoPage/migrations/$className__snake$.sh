#!/bin/bash

if grep -Fxp $className;format="snake"$ applied
then
    echo "Migration $className;format="snake"$ has already been applied, exiting"
    exit 1
fi

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$className;format="decap"$                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.$className$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /$className;format="decap"$                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.$className$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /change$className$                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.$className$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /change$className$                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.$className$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def $className;format="decap"$: Option[Boolean] = cacheMap.getEntry[Boolean]($className$Id.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def $className;format="decap"$: Option[AnswerRow] = userAnswers.$className;format="decap"$ map {";\
     print "    x => AnswerRow(\"$className;format="decap"$.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.$className$Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as applied"
echo "$className;format="snake"$" >> applied
