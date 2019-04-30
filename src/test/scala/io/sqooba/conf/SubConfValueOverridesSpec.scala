package io.sqooba.conf

import org.scalatest.{FlatSpec, Matchers}

class SubConfValueOverridesSpec extends FlatSpec with Matchers {

  /*
  subConf {
    another {
      aString = "this is a string"
      aInt = 123
    }
    rootString = "root"
  }
  */

  "subconfig" should "contain overrides" in {
    val conf = SqConf.default().getConfig("subConf").withOverrides(Map("subConf.root" -> "newRoot"))
    conf.getString("root") shouldBe "newRoot"
  }

  "subconfig" should "contain overrides 2" in {
    val conf = SqConf.default().withOverrides(Map("subConf.root" -> "newRoot")).getConfig("subConf")
    conf.getString("root") shouldBe "newRoot"
  }

  "value overrides" should "append prefix" in {
    val conf = SqConf.default().getConfig("subConf").withOverrides(Map("root" -> "newRoot"))
    conf.getString("root") shouldBe "newRoot"
  }

}
