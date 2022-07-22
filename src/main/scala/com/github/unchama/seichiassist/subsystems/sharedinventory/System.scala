package com.github.unchama.seichiassist.subsystems.sharedinventory

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, SyncEffect}
import com.github.unchama.datarepository.KeyedDataRepository
import com.github.unchama.datarepository.bukkit.player.BukkitRepositoryControls
import com.github.unchama.generic.ContextCoercion
import com.github.unchama.minecraft.bukkit.algebra.BukkitPlayerHasUuid.instance
import com.github.unchama.seichiassist.meta.subsystem.Subsystem
import com.github.unchama.seichiassist.subsystems.sharedinventory.application.repository.SharedInventoryRepositoryDefinition
import com.github.unchama.seichiassist.subsystems.sharedinventory.bukkit.command.ShareInventoryCommand
import com.github.unchama.seichiassist.subsystems.sharedinventory.domain.SharedFlag
import com.github.unchama.seichiassist.subsystems.sharedinventory.domain.bukkit.InventoryContents
import com.github.unchama.seichiassist.subsystems.sharedinventory.infrastracture.JdbcSharedInventoryPersistence
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

import java.util.UUID

trait System[F[_]] extends Subsystem[F] {
  val api: SharedInventoryAPI[F, Player]
}

object System {

  import cats.implicits._

  def wired[F[_]: SyncEffect: ContextCoercion[*[_], G], G[_]: ConcurrentEffect]
    : G[System[G]] = {
    val persistence = new JdbcSharedInventoryPersistence[F]

    for {
      sharedFlagRepositoryControls <-
        ContextCoercion(
          BukkitRepositoryControls.createHandles(
            SharedInventoryRepositoryDefinition.withContext[F, G, Player](persistence)
          )
        )
    } yield {
      new System[G] {
        override implicit val api: SharedInventoryAPI[G, Player] =
          new SharedInventoryAPI[G, Player] {
            override def save(targetUuid: UUID, inventoryContents: InventoryContents): G[Unit] =
              ContextCoercion(persistence.save(targetUuid, inventoryContents))

            override def clear(targetUuid: UUID): G[Unit] =
              ContextCoercion(persistence.clear(targetUuid))

            override def load(targetUuid: UUID): G[Option[InventoryContents]] =
              ContextCoercion(persistence.load(targetUuid))

            override val sharedFlagRepository
              : KeyedDataRepository[Player, Ref[G, InventoryContents]] =
              sharedFlagRepositoryControls
                .repository
                .map(value => value.sharedFlag.mapK(ContextCoercion.asFunctionK))

            override def setSharing(player: Player): G[Unit] =
              ContextCoercion(
                sharedFlagRepositoryControls
                  .repository(player)
                  .sharedFlag
                  .set(SharedFlag.Sharing)
              )

            override def setNotSharing(player: Player): G[Unit] =
              ContextCoercion(
                sharedFlagRepositoryControls
                  .repository(player)
                  .sharedFlag
                  .set(SharedFlag.NotSharing)
              )

          }

        override val managedRepositoryControls: Seq[BukkitRepositoryControls[G, _]] =
          Seq(sharedFlagRepositoryControls.coerceFinalizationContextTo[G])

        override val commands: Map[String, TabExecutor] = {
          Map("shareinv" -> new ShareInventoryCommand[G].executor)
        }
      }
    }
  }

}
