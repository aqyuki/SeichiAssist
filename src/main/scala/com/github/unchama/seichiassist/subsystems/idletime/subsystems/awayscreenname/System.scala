package com.github.unchama.seichiassist.subsystems.idletime.subsystems.awayscreenname

import cats.effect.{ContextShift, IO, LiftIO, Sync, SyncIO}
import com.github.unchama.concurrent.RepeatingTaskContext
import com.github.unchama.datarepository.bukkit.player.{
  BukkitRepositoryControls,
  PlayerDataRepository
}
import com.github.unchama.datarepository.template.RepositoryDefinition
import com.github.unchama.minecraft.actions.OnMinecraftServerThread
import com.github.unchama.seichiassist.meta.subsystem.Subsystem
import com.github.unchama.seichiassist.subsystems.idletime.application.repository.IdleMinuteRepositoryDefinitions
import com.github.unchama.seichiassist.subsystems.idletime.domain.PlayerIdleMinuteRepository
import com.github.unchama.seichiassist.subsystems.idletime.subsystems.awayscreenname.application.repository.{
  PlayerAwayTimeRecalculationRoutineFiberRepositoryDefinitions,
  PlayerLocationRepositoryDefinitions
}
import com.github.unchama.seichiassist.subsystems.idletime.subsystems.awayscreenname.bukkit.{
  BukkitPlayerLocationRepository,
  BukkitUpdatePlayerScreenName
}
import com.github.unchama.seichiassist.subsystems.idletime.subsystems.awayscreenname.bukkit.routines.BukkitPlayerAwayTimeRecalculationRoutine
import com.github.unchama.seichiassist.subsystems.idletime.subsystems.awayscreenname.domain.{
  PlayerLocationRepository,
  UpdatePlayerScreenName
}
import org.bukkit.Location
import org.bukkit.entity.Player

object System {

  def wired[F[_]: Sync: LiftIO](
    implicit repeatingTaskContext: RepeatingTaskContext,
    onMainThread: OnMinecraftServerThread[IO],
    ioShift: ContextShift[IO]
  ): SyncIO[Subsystem[F]] = {
    implicit val playerLocationRepository
      : Player => PlayerLocationRepository[SyncIO, Location, Player] =
      new BukkitPlayerLocationRepository[SyncIO](_)

    for {
      idleMinuteRepositoryControls <- BukkitRepositoryControls.createHandles(
        RepositoryDefinition
          .Phased
          .TwoPhased(
            IdleMinuteRepositoryDefinitions.initialization[SyncIO, Player],
            IdleMinuteRepositoryDefinitions.finalization[SyncIO, Player]
          )
      )
      playerLocationRepositoryControls <- BukkitRepositoryControls.createHandles(
        RepositoryDefinition
          .Phased
          .TwoPhased(
            PlayerLocationRepositoryDefinitions.initialization[SyncIO, Location, Player],
            PlayerLocationRepositoryDefinitions.finalization[SyncIO, Location, Player]
          )
      )
      playerAwayTimeRecalculationRoutineFiberRepositoryControls <- BukkitRepositoryControls
        .createHandles(
          RepositoryDefinition
            .Phased
            .TwoPhased(
              PlayerAwayTimeRecalculationRoutineFiberRepositoryDefinitions
                .initialization[SyncIO, Player] { player =>
                  implicit val playerLocationRepository
                    : PlayerDataRepository[PlayerLocationRepository[SyncIO, Location, Player]] =
                    playerLocationRepositoryControls.repository
                  implicit val idleMinuteRepository
                    : PlayerDataRepository[PlayerIdleMinuteRepository[SyncIO]] =
                    idleMinuteRepositoryControls.repository
                  implicit val updatePlayerScreenName: UpdatePlayerScreenName[SyncIO, Player] =
                    new BukkitUpdatePlayerScreenName[SyncIO]

                  new BukkitPlayerAwayTimeRecalculationRoutine(player)
                },
              PlayerAwayTimeRecalculationRoutineFiberRepositoryDefinitions
                .finalization[SyncIO, Player]
            )
        )
    } yield {
      new Subsystem[F] {
        override val managedRepositoryControls: Seq[BukkitRepositoryControls[F, _]] =
          Seq(
            idleMinuteRepositoryControls,
            playerLocationRepositoryControls,
            playerAwayTimeRecalculationRoutineFiberRepositoryControls
          ).map(_.coerceFinalizationContextTo[F])
      }
    }
  }

}
