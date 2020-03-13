package getVisibility.calc.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.softwaremill.macwire._
import getVisibility.calc.api.CalculateService

import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents

import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents


class CalculateServiceLoader  extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
  new CalculateServiceApplication(context)  {
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new CalculateServiceApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[CalculateService])
}

abstract class CalculateServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with LagomKafkaClientComponents
    with AhcWSComponents
    {
  override lazy val lagomServer: LagomServer = serverFor[CalculateService](wire[CalculateServiceImpl])
}
