create database if not exists seichiassist character set utf8 collate utf8_general_ci;

use seichiassist;

create table if not exists gachadata
(
    id     int auto_increment unique,
    amount int(11)
);
alter table gachadata
    add column if not exists probability double default 0.0,
    add column if not exists itemstack   blob   default null;

create table if not exists msgachadata
(
    id     int auto_increment unique,
    amount int(11)
);
alter table msgachadata
    add column if not exists probability double   default 0.0,
    add column if not exists level       int(11)  default 0,
    add column if not exists obj_name    tinytext default null,
    add column if not exists itemstack   blob     default null;

create table if not exists donatedata
(
    id int auto_increment unique
);
alter table donatedata
    add column if not exists playername varchar(20)  default null,
    add column if not exists playeruuid varchar(128) default null,
    add column if not exists effectnum  int          default null,
    add column if not exists effectname varchar(20)  default null,
    add column if not exists getpoint   int          default 0,
    add column if not exists usepoint   int          default 0,
    add column if not exists date       datetime     default null;

create table if not exists playerdata
(
    name varchar(30) unique,
    uuid varchar(128) unique
);
alter table playerdata
    add column if not exists effectflag               tinyint          default 0,
    add column if not exists minestackflag            boolean          default true,
    add column if not exists messageflag              boolean          default false,
    add column if not exists activemineflagnum        int              default 0,
    add column if not exists assaultflag              boolean          default false,
    add column if not exists activeskilltype          int              default 0,
    add column if not exists activeskillnum           int              default 1,
    add column if not exists assaultskilltype         int              default 0,
    add column if not exists assaultskillnum          int              default 0,
    add column if not exists arrowskill               int              default 0,
    add column if not exists multiskill               int              default 0,
    add column if not exists breakskill               int              default 0,
    add column if not exists fluidcondenskill         int              default 0,
    add column if not exists watercondenskill         int              default 0,
    add column if not exists lavacondenskill          int              default 0,
    add column if not exists effectnum                int              default 0,
    add column if not exists gachapoint               int              default 0,
    add column if not exists gachaflag                boolean          default true,
    add column if not exists level                    int              default 1,
    add column if not exists numofsorryforbug         int              default 0,
    add column if not exists inventory                blob             default null,
    add column if not exists rgnum                    int              default 0,
    add column if not exists totalbreaknum            bigint           default 0,
    add column if not exists lastquit                 datetime         default null,
    add column if not exists lastcheckdate            varchar(12)      default null,
    add column if not exists ChainJoin                int              default 0,
    add column if not exists TotalJoin                int              default 0,
    add column if not exists LimitedLoginCount        int              default 0,
    add column if not exists displayTypeLv            boolean          default true,
    add column if not exists displayTitleNo           int              default 0,
    add column if not exists displayTitle1No          int              default 0,
    add column if not exists displayTitle2No          int              default 0,
    add column if not exists displayTitle3No          int              default 0,
    add column if not exists TitleFlags               text             default null,
    add column if not exists giveachvNo               int              default 0,
    add column if not exists achvPointMAX             int              default 0,
    add column if not exists achvPointUSE             int              default 0,
    add column if not exists achvChangenum            int              default 0,
    add column if not exists starlevel                int              default 0,
    add column if not exists starlevel_Break          int              default 0,
    add column if not exists starlevel_Time           int              default 0,
    add column if not exists starlevel_Event          int              default 0,
    add column if not exists playtick                 int              default 0,
    add column if not exists killlogflag              boolean          default false,
    add column if not exists worldguardlogflag        boolean          default true,
    add column if not exists multipleidbreakflag      boolean          default false,
    add column if not exists pvpflag                  boolean          default false,
    add column if not exists loginflag                boolean          default false,
    add column if not exists p_vote                   int              default 0,
    add column if not exists p_givenvote              int              default 0,
    add column if not exists effectpoint              int              default 0,
    add column if not exists premiumeffectpoint       int              default 0,
    add column if not exists mana                     double           default 0.0,
    add column if not exists expvisible               boolean          default true,
    add column if not exists totalexp                 int              default 0,
    add column if not exists expmarge                 tinyint unsigned default 0,
    add column if not exists shareinv                 blob,
    add column if not exists everysound               boolean          default true,
    add column if not exists everymessage             boolean          default true,
    add column if not exists build_lv                 int              default 1,
    add column if not exists build_count              double           default 0,
    add column if not exists build_count_flg          TINYINT UNSIGNED default 0,
    add column if not exists anniversary              boolean          default false,
    add column if not exists canVotingFairyUse        boolean          default false,
    add column if not exists newVotingFairyTime       varchar(17)      default '',
    add column if not exists VotingFairyRecoveryValue int              default 0,
    add column if not exists hasVotingFairyMana       int              default 0,
    add column if not exists toggleGiveApple          int              default 1,
    add column if not exists toggleVotingFairy        int              default 1,
    add column if not exists p_apple                  bigint           default 0,
    add column if not exists contribute_point         int              default 0,
    add column if not exists added_mana               int              default 0,
    add column if not exists lastvote                 varchar(40)      default null,
    add column if not exists chainvote                int              default 0,
    add column if not exists GBstage                  int              default 0,
    add column if not exists GBexp                    int              default 0,
    add column if not exists GBlevel                  int              default 0,
    add column if not exists isGBStageUp              boolean          default false,
    add column if not exists hasNewYearSobaGive       boolean          default false,
    add column if not exists newYearBagAmount         int              default 0,
    add column if not exists hasChocoGave             boolean          default false;

