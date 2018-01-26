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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

@Singleton
class PartnerEmploymentIncomePYForm @Inject() (appConfig: FrontendAppConfig) extends IncomeFormatter {

  override val minValue: Double = appConfig.minIncome
  override val maxValue: Double = appConfig.maxIncome

  override val errorKeyBlank: String = partnerEmploymentIncomePYRequiredErrorKey
  override val errorKeyInvalid: String = partnerEmploymentIncomePYInvalidErrorKey
}