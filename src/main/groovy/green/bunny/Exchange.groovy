package green.bunny

import groovy.transform.TypeChecked

@TypeChecked
class Exchange {
  public static Set<String> KNOWN_EXCHANGE_TYPES =
      ["direct", "fanout", "topic", "headers"].toSet().asImmutable()

  protected boolean durable
  protected boolean autoDelete

  protected Map<String, Object> arguments

  protected String type
  protected String name
  protected Channel channel

  //
  // Constructors
  //

  Exchange(Channel ch, String name, String type) {
    this(ch, type, name, false, false, [:])
  }

  Exchange(Channel ch, String name, String type,
           boolean durable, boolean autoDelete,
          Map<String, Object> arguments) {
    validateType(type)

    this.channel    = ch
    this.name       = name
    this.type       = type
    this.durable    = durable
    this.autoDelete = autoDelete
    this.arguments  = arguments
  }

  //
  // Properties
  //

  def String getName() {
    this.name
  }

  def boolean getIsPredefined() {
    this.name.isEmpty() || this.name.startsWith("amq.")
  }

  def String getType() {
    this.type
  }

  def Channel getChannel() {
    this.channel
  }

  def boolean getIsDurable() {
    this.durable
  }

  def boolean getIsAutoDelete() {
    this.autoDelete
  }

  def Map<String, Object> getArguments() {
    this.arguments
  }

  //
  // Publishing
  //

  def void publish(String payload) {
    publish([:], payload)
  }

  def void publish(byte[] payload) {
    publish([:], payload)
  }

  def void publish(Map<String, Object> opts, String payload) {
    this.channel.basicPublish(opts, this.name, payload)
  }

  def void publish(Map<String, Object> opts, byte[] payload) {
    this.channel.basicPublish(opts, this.name, payload)
  }

  //
  // Binding
  //

  def Exchange bind(Exchange source) {
    this.channel.exchangeBind(this.name, source.name, "")
    this
  }

  def Exchange bind(Map<String, Object> opts, Exchange source) {
    this.channel.exchangeBind(this.name, source.name,
        opts.get("routingKey") as String,
        opts.get("arguments") as Map<String, Object>)
  }

  //
  // Deletion
  //

  def delete() {
    this.channel.exchangeDelete(this.name)
  }

  //
  // Implementation
  //

  @Override
  def String toString() {
    "<" +
        "type = " + type +
        ", name = " + name +
        ", durable = " + isDurable.toString() +
        ", autoDelete = " + isAutoDelete.toString() +
    ">"
  }

  def maybePerformDeclare() {
    if(!isPredefined) {
      this.channel.exchangeDeclare(name, type, durable, autoDelete, arguments)
    }
  }

  static validateType(String s) {
    if(!(KNOWN_EXCHANGE_TYPES.contains(s) ||
        s.startsWith("x-"))) {
      throw new IllegalArgumentException("Invalid exchange type: " + s.toString())
    }
  }
}
