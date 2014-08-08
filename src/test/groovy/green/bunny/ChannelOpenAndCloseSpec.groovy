package green.bunny

class ChannelOpenAndCloseSpec extends IntegrationSpec {
  def "opening a channel with an automatically allocated number"() {
    when: "client opens a channel without explicitly providing a channel number"
    def ch = conn.createChannel()

    then: "operation succeeds"
    ch.isOpen
  }
}
