package domain

case class User(id: Int, name: String, email: Option[String])
case class Transaction(tranId: String, tranTs: String, clientId: String, pdType: String, symbol: String, unitNum: Integer, unitPrice: Double)
