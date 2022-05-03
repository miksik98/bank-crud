package crud.server.bank.db.query

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ClientSortFieldTest extends AnyFunSuite with Matchers {
  test("Applying fromString method with illegal parameter is Invalid") {
    ClientSortField.fromString("x").isInvalid shouldBe true
  }

  test("Domain fields should be mapped for enums") {
    def validTest[T](s: String, f: ClientSortField[T]) = ClientSortField.fromString(s).toOption shouldBe Some(f)
    validTest("name", ClientSortField.Name)
    validTest("surname", ClientSortField.Surname)
    validTest("birthYear", ClientSortField.BirthYear)
    validTest("money", ClientSortField.Money)
  }
}
