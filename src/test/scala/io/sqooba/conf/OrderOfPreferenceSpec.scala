package io.sqooba.conf

import org.scalatest.{FlatSpec, Matchers}

class OrderOfPreferenceSpec extends FlatSpec with Matchers {

  val oop: List[OrderOfPreference.Value] = List(OrderOfPreference.ENV_VARIABLE,
    OrderOfPreference.CONF_FIlE,
    OrderOfPreference.VALUE_OVERRIDES)

  "default conf" should "have preference: overrides, env, file" in {
    val conf = new SqConf()
    conf.getOrderOfPreference shouldBe List(OrderOfPreference.VALUE_OVERRIDES,
      OrderOfPreference.ENV_VARIABLE,
      OrderOfPreference.CONF_FIlE)
  }

  "conf" should "have configured order preference" in {
    val conf = new SqConf().configureOrder(oop)
    conf.getOrderOfPreference shouldBe oop
  }

  "simple value" should "be given according to default order" in {
    val conf = new SqConf().withOverrides(Map("subConf.rootString" -> "newRoot"))
    val res = conf.getValueAccordingOrderOfOfPreference[String]("subConf.rootString", (st: String) => st)
    res shouldBe "newRoot"
  }

  it should "be given according to parameters" in {
    val conf = new SqConf().withOverrides(Map("subConf.rootString" -> "newRoot")).configureOrder(oop)
    val res = conf.getValueAccordingOrderOfOfPreference[String]("subConf.rootString", (st: String) => st)
    res shouldBe "root"
    res shouldBe conf.getString("subConf.rootString")
  }

  it should "use overrides if default order" in {
    val conf = new SqConf().withOverrides(Map("subConf.rootString" -> "newRoot"))
    val res = conf.getValueAccordingOrderOfOfPreference[String]("subConf.rootString", (st: String) => st)
    res shouldBe "newRoot"
    res shouldBe conf.getString("subConf.rootString")
  }

  it should "be given according to parameters in correct order" in {
    val conf = new SqConf().withOverrides(Map("subConf.rootString" -> "newRoot")).configureOrder(oop)
    EnvUtil.setEnv(conf.keyAsEnv("subConf.rootString"), "envRootString")
    val res = conf.getValueAccordingOrderOfOfPreference[String]("subConf.rootString", (st: String) => st)
    val res2 = conf.getString("subConf.rootString")
    res shouldBe res2
    res shouldBe "envRootString"
    EnvUtil.removeEnv(conf.keyAsEnv("subConf.rootString"))
  }

  "list of items" should "be given according to order of preference" in {
    val conf = new SqConf().withOverrides(Map("subConf.intList" -> "5,6,7"))
    val res = conf.getListOfValuesAccordingOrderOfPreference[Int]("subConf.intList", (st: String) => st.toInt)

    res shouldBe List(5,6,7)
    res shouldBe conf.getListOfInt("subConf.intList")
  }

  it should "be given according to order of preference when not default, file is higher than overrides" in {
    val conf = new SqConf().withOverrides(Map("subConf.intList" -> "5,6,7")).configureOrder(oop)
    val res = conf.getListOfValuesAccordingOrderOfPreference[Int]("subConf.intList", (st: String) => st.toInt)

    res shouldBe List(1,2,3)
    res shouldBe conf.getListOfInt("subConf.intList")
  }

  "configured order of preference" should "be the same in subconfig" in {
    val conf = new SqConf().withOverrides(Map("subConf.rootString" -> "newRoot")).configureOrder(oop)
    val subConf = conf.getSubConfig("subConf")
    val res = conf.getString("subConf.rootString")
    val subRes = subConf.getString("rootString")
    res shouldBe "root"
    res shouldBe subRes
  }
}
