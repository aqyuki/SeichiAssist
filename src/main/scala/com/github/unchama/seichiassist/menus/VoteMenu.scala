package com.github.unchama.seichiassist.menus

import cats.effect.{ConcurrentEffect, IO, SyncIO}
import com.github.unchama.itemstackbuilder.IconItemStackBuilder
import com.github.unchama.menuinventory.router.CanOpen
import com.github.unchama.menuinventory.slot.button.Button
import com.github.unchama.menuinventory.slot.button.action.LeftClickButtonEffect
import com.github.unchama.menuinventory.syntax.IntInventorySizeOps
import com.github.unchama.menuinventory.{ChestSlotRef, Menu, MenuFrame, MenuSlotLayout}
import com.github.unchama.seichiassist.concurrent.PluginExecutionContexts
import com.github.unchama.seichiassist.concurrent.PluginExecutionContexts.onMainThread
import com.github.unchama.seichiassist.menus.stickmenu.FirstPage
import com.github.unchama.seichiassist.subsystems.breakcount.BreakCountAPI
import com.github.unchama.seichiassist.subsystems.mana.ManaApi
import com.github.unchama.seichiassist.subsystems.vote.VoteAPI
import com.github.unchama.seichiassist.subsystems.vote.bukkit.actions.BukkitReceiveVoteBenefits
import com.github.unchama.seichiassist.subsystems.vote.subsystems.fairy.FairyAPI
import com.github.unchama.seichiassist.subsystems.vote.subsystems.fairy.bukkit.FairySpeech
import com.github.unchama.seichiassist.subsystems.vote.subsystems.fairy.bukkit.actions.BukkitSummonFairy
import com.github.unchama.seichiassist.subsystems.vote.subsystems.fairy.domain.property.{
  AppleOpenState,
  FairyPlaySound,
  FairySummonCost
}
import com.github.unchama.seichiassist.task.VotingFairyTask
import com.github.unchama.targetedeffect.commandsender.MessageEffect
import com.github.unchama.targetedeffect.player.FocusedSoundEffect
import com.github.unchama.targetedeffect.player.PlayerEffects.closeInventoryEffect
import com.github.unchama.targetedeffect.{SequentialEffect, TargetedEffect, UnfocusedEffect}
import org.bukkit.ChatColor._
import org.bukkit.entity.Player
import org.bukkit.{Material, Sound}

import java.util.UUID

object VoteMenu extends Menu {

  class Environment(
    implicit val voteAPI: VoteAPI[IO, Player],
    val fairyAPI: FairyAPI[IO, SyncIO, Player],
    val breakCountAPI: BreakCountAPI[IO, SyncIO, Player],
    val manaApi: ManaApi[IO, SyncIO, Player],
    val ioCanOpenFirstPage: IO CanOpen FirstPage.type
  )

  /**
   * メニューのサイズとタイトルに関する情報
   */
  override val frame: MenuFrame = MenuFrame(4.chestRows, s"$DARK_PURPLE${BOLD}投票ptメニュー")

  /**
   * @return
   * `player`からメニューの[[MenuSlotLayout]]を計算する[[IO]]
   */
  override def computeMenuLayout(
    player: Player
  )(implicit environment: Environment): IO[MenuSlotLayout] = {
    import ConstantButtons._
    import environment._
    import eu.timepit.refined.auto._

    val uuid = player.getUniqueId

    val buttons =
      Map(
        ChestSlotRef(0, 0) -> receiveVoteBenefitsButton(player),
        ChestSlotRef(0, 2) -> fairySummonTimeToggleButton(player),
        ChestSlotRef(0, 4) -> fairySummonButton(player),
        ChestSlotRef(1, 0) -> showVoteURLButton,
        ChestSlotRef(1, 2) -> fairyContractSettingToggle(uuid),
        ChestSlotRef(2, 2) -> fairyPlaySoundToggleButton(uuid),
        ChestSlotRef(3, 0) -> CommonButtons.openStickMenu
      )

    IO(MenuSlotLayout(buttons))
  }

  private object ConstantButtons {

    private implicit val ioCE: ConcurrentEffect[IO] =
      IO.ioConcurrentEffect(PluginExecutionContexts.asyncShift)

    def receiveVoteBenefitsButton(player: Player)(
      implicit voteAPI: VoteAPI[IO, Player],
      breakCountAPI: BreakCountAPI[IO, SyncIO, Player]
    ): Button = {
      val uuid = player.getUniqueId
      for {
        benefits <- voteAPI.receivedVoteBenefits(uuid)
        voteCounter <- voteAPI.voteCounter(uuid)
        effectPoint <- voteAPI.effectPoints(player)
      } yield {
        Button(
          new IconItemStackBuilder(Material.DIAMOND)
            .title(s"$LIGHT_PURPLE$UNDERLINE${BOLD}クリックで投票特典を受け取れます")
            .lore(
              List(
                s"$RESET${GRAY}投票特典を受け取るには",
                s"$RESET${GRAY}投票ページで投票した後",
                s"$RESET${AQUA}特典受け取り済み投票回数: ${benefits.value}",
                s"$RESET${AQUA}特典未受け取り投票係数: ${voteCounter.value - benefits.value}",
                s"$RESET${AQUA}所有pt: ${effectPoint.value}"
              )
            )
            .enchanted()
            .build(),
          LeftClickButtonEffect {
            SequentialEffect(
              TargetedEffect.delay { player =>
                BukkitReceiveVoteBenefits[IO, SyncIO].receive(player).unsafeRunSync()
              },
              MessageEffect(
                s"${GOLD}投票特典$WHITE(${voteCounter.value - benefits.value}票分)を受け取りました"
              )
            )
          }
        )
      }
    }.unsafeRunSync()

    val showVoteURLButton: Button = Button(
      new IconItemStackBuilder(Material.BOOK_AND_QUILL)
        .title(s"$YELLOW$UNDERLINE${BOLD}投票ページにアクセス")
        .lore(
          List(
            s"$RESET${GREEN}投票すると様々な特典が！",
            s"$RESET${GREEN}1日1回投票できます",
            s"$RESET${DARK_GRAY}クリックするとチャット欄に",
            s"$RESET${DARK_GRAY}URLが表示されますので",
            s"$RESET${DARK_GRAY}Tキーを押してから",
            s"$RESET${DARK_GRAY}そのURLをクリックしてください"
          )
        )
        .build(),
      LeftClickButtonEffect {
        SequentialEffect(
          MessageEffect(
            List(
              s"$RED${UNDERLINE}https://minecraft.jp/servers/54d3529e4ddda180780041a7/vote",
              s"$RED${UNDERLINE}https://monocraft.net/servers/Cf3BffNIRMERDNbAfWQm"
            )
          ),
          FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f),
          closeInventoryEffect
        )
      }
    )

    def fairySummonTimeToggleButton(
      player: Player
    )(implicit fairyAPI: FairyAPI[IO, SyncIO, Player]): Button = {
      val fairySummonCost = fairyAPI.fairySummonCost(player).unsafeRunSync()
      Button(
        new IconItemStackBuilder(Material.WATCH)
          .title(s"$AQUA$UNDERLINE${BOLD}マナ妖精 時間設定")
          .lore(
            List(
              s"$RESET$GREEN$BOLD${VotingFairyTask.dispToggleVFTime(fairySummonCost.value)}",
              "",
              s"$RESET${GRAY}コスト",
              s"$RESET$RED$BOLD${fairySummonCost.value * 2}投票pt",
              "",
              s"$RESET$DARK_RED${UNDERLINE}クリックで切り替え"
            )
          )
          .build(),
        LeftClickButtonEffect {
          SequentialEffect(
            FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f),
            UnfocusedEffect(
              fairyAPI.updateFairySummonCost(
                player.getUniqueId,
                FairySummonCost(fairySummonCost.value % 4 + 1)
              )
            )
          )
        }
      )
    }

    def fairyContractSettingToggle(
      uuid: UUID
    )(implicit fairyAPI: FairyAPI[IO, SyncIO, Player]): Button =
      Button(
        new IconItemStackBuilder(Material.PAPER)
          .title(s"$GOLD$UNDERLINE${BOLD}妖精とのお約束")
          .lore(fairyAPI.getFairyLore(uuid).unsafeRunSync().lore.toList)
          .build(),
        LeftClickButtonEffect {
          SequentialEffect(
            UnfocusedEffect {
              fairyAPI.updateAppleOpenState(
                uuid,
                AppleOpenState
                  .values
                  .find(
                    _.amount == fairyAPI.appleOpenState(uuid).unsafeRunSync().amount % 4 + 1
                  )
                  .get
              )
            },
            FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f)
          )
        }
      )

    def fairyPlaySoundToggleButton(
      uuid: UUID
    )(implicit fairyAPI: FairyAPI[IO, SyncIO, Player]): Button = {
      val description =
        List(s"$RESET$DARK_GRAY※この機能はデフォルトでONです。", s"$RESET$DARK_RED${UNDERLINE}クリックで切り替え")
      val playSoundOnLore = List(s"$RESET${GREEN}現在音が鳴る設定になっています。") ++ description
      val playSoundOffLore = List(s"$RESET${GREEN}現在音が鳴らない設定になっています。") ++ description

      Button(
        new IconItemStackBuilder(Material.JUKEBOX)
          .title(s"$GOLD$UNDERLINE${BOLD}マナ妖精の音トグル")
          .lore(
            if (fairyAPI.fairyPlaySound(uuid).unsafeRunSync() == FairyPlaySound.on)
              playSoundOnLore
            else playSoundOffLore
          )
          .build(),
        LeftClickButtonEffect {
          SequentialEffect(
            FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f),
            UnfocusedEffect {
              fairyAPI.fairyPlaySoundToggle(uuid).unsafeRunAsyncAndForget()
            }
          )
        }
      )
    }

    def fairySummonButton(player: Player)(
      implicit fairyAPI: FairyAPI[IO, SyncIO, Player],
      voteAPI: VoteAPI[IO, Player],
      breakCountAPI: BreakCountAPI[IO, SyncIO, Player],
      manaApi: ManaApi[IO, SyncIO, Player]
    ): Button = {
      val fairySummonState =
        fairyAPI.fairySummonCost(player).unsafeRunSync().value
      Button(
        new IconItemStackBuilder(Material.GHAST_TEAR)
          .title(s"$LIGHT_PURPLE$UNDERLINE${BOLD}マナ妖精 召喚")
          .lore(
            List(
              s"$RESET$GRAY${fairySummonState * 2}投票ptを消費して",
              s"$RESET${GRAY}マナ妖精を呼びます",
              s"$RESET${GRAY}時間: ${VotingFairyTask.dispToggleVFTime(fairySummonState)}",
              s"$RESET${DARK_RED}Lv.10以上で開放"
            )
          )
          .enchanted()
          .build(),
        LeftClickButtonEffect {
          SequentialEffect(
            UnfocusedEffect {
              BukkitSummonFairy(player).summon.unsafeRunSync()
            },
            closeInventoryEffect
          )
        }
      )
    }

    def checkTimeButton(
      player: Player
    )(implicit fairyAPI: FairyAPI[IO, SyncIO, Player]): Button = {
      Button(
        new IconItemStackBuilder(Material.COMPASS)
          .title(s"$LIGHT_PURPLE$UNDERLINE${BOLD}マナ妖精に時間を聞く")
          .lore(List(s"$RESET${GRAY}妖精さんはいそがしい。", s"${GRAY}帰っちゃう時間を教えてくれる"))
          .enchanted()
          .build(),
        LeftClickButtonEffect {
          SequentialEffect(
            UnfocusedEffect {
              new FairySpeech[IO, SyncIO]().speechEndTime(player).unsafeRunSync()
            },
            closeInventoryEffect
          )
        }
      )
    }

    def gachaRingoInformation(player: Player): Button = {
      Button(
        new IconItemStackBuilder(Material.GOLDEN_APPLE)
          .title(s"$YELLOW$UNDERLINE$BOLD㊙ がちゃりんご情報 ㊙")
          .lore(
            List(
              s"$RESET$RED$BOLD※ﾆﾝｹﾞﾝに見られないように気を付けること！",
              s"$RESET$RED$BOLD  毎日大妖精からデータを更新すること！",
              "",
              s"$RESET$GOLD${BOLD}昨日までにがちゃりんごを",
              s"$RESET$GOLD${BOLD}たくさんくれたﾆﾝｹﾞﾝたち",
              s"$RESET${DARK_GRAY}召喚されたらラッキーだよ！"
            )
          )
          .enchanted()
          .build()
      )
    }

  }
}
