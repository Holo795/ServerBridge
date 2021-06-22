# Mineweb
## Comment récupérer le token de son bot discord ?

Vous devez tout d'abord créer une “application” sur [Discord](https://discord.com/) pour faire fonctionner votre bot. Le but est d'obtenir un “**token** d'autorisation” pour le **bot** afin que discord reconnaisse votre bot sur ses serveurs. 

![](https://cdn.discordapp.com/attachments/648266540495405057/856959512794759218/unknown.png)

Tout d'abord, rendez-vous sur https://discordapp.com/developers/applications/me. Votre compte discord doit y être connecté, vous accéderez donc directement à la liste des applications de votre compte.
Cliquez sur « New Appliccation» pour commencer. Donnez un nom au bot (et une photo de profil si vous le souhaitez) , puis cliquez sur le bouton “Create”.

Maintenant, dans le menu de gauche, cliquez sur Bot. Une fois dans le nouveau menu, cliquez sur Add Bot sous l’option Build-a-bot. Si vous n’avez qu’une seule application – celle que nous venons de créer – elle devrait apparaître automatiquement. Sinon, sélectionnez-la.

![](https://cdn.discordapp.com/attachments/648266540495405057/856960186321600522/unknown.png)

Dans la case “Build-A-Bot” , cherchez les mots “Click to reveal Token” : Cliquez pour afficher. Cliquez sur ce lien et vous verrez apparaître une chaîne de texte. C’est le jeton d’autorisation de votre bot (token), qui vous permettera de lier votre bot au plugin en faisant un **/mineweb token** **\<votre token>** (sous bukkit, spigot ou paper) ou tout simplement directement dans le fichier config pour les personnes sous bungeecord.

![](https://cdn.discordapp.com/attachments/648266540495405057/856960602655162388/unknown.png)
<font size="1"> Ne le partagez avec personne – ce jeton permet à celui qui l’a de créer un code pour le bot, ce qui signifie que celui qui l’a peut contrôler votre bot. Si vous pensez que le jeton a été compromis, la bonne nouvelle est que vous pouvez facilement en générer un nouveau grâce au lien situé juste sous le jeton, qui dit “Regenerate”. </font>

Pensez a mettre votre bot en privé pour que personne ne puisse l'ajouter à votre place
![](https://cdn.discordapp.com/attachments/648266540495405057/856962463911772200/unknown.png)
### Ajoutez son bot
Pour ajouter le bot discord sur votre serveur il vous suffira d'avoir les permissions nécessaires et d'utiliser ce lien https://discord.com/oauth2/authorize?client_id=ID_DE_VOTRE_BOT&scope=bot&permissions=8 en remplaçant "ID_DE_VOTRE_BOT" par son id se trouvant ici : 

![](https://cdn.discordapp.com/attachments/648266540495405057/856964183291592764/unknown.png)
