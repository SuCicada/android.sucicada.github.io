package org.peng.sucicada

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.TextProgressMonitor
import org.peng.sucicada.Util.log
import java.io.File
import java.io.IOException
import java.net.*
import java.util.*

class GitDownloader(private val assetsDir: String) {
    var overFlag = false
    fun isOver(): Boolean {
        return overFlag
    }

    fun syncAssets() {
        val remoteUrl = "https://github.com/SuCicada/sucicada.github.io.git"
        val assetsDirFile = File(assetsDir)
        log("$assetsDir : ${assetsDirFile.exists()}")
        log("git : $remoteUrl")

//            File("")
//        val proxyAddress = "http://" + proxyHost.toString() + ":" + proxyPort
//        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
//        git.pull().setCredentialsProvider(cp).call();
//            .setCredentialsProvider(cp)
        //        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
//        git.pull().setCredentialsProvider(cp).call();
//            .setCredentialsProvider(cp)
        val git: Git
        if (assetsDirFile.exists() && Arrays.asList(assetsDirFile.list())
                .filter { n -> n.equals("index.html") }
                .count() != 1
        ) {
            assetsDirFile.delete()
            log("delete $assetsDir")
        }
        if (!assetsDirFile.exists()) {
            log("git clone start")

            val cmd = Git.cloneRepository()
                .setProgressMonitor(TextProgressMonitor())
                .setURI(remoteUrl)
                .setDirectory(assetsDirFile)
//            setGitConfig(cmd.repository)
            git = cmd.call()

        } else {
            log("git pull start")
            val lockFile = File(assetsDir, ".git/index.lock")
            if (lockFile.exists()) {
                lockFile.delete()
            }
            git = Git.open(assetsDirFile)
            //            Repository localRepo = new FileRepository(localPath);
//            git = new Git(localRepo);
//        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, pass);
            setGitConfig(git.repository)

            git.fetch()
                .setForceUpdate(true)
                .setProgressMonitor(TextProgressMonitor())
                .call()
            git.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .call()
//            git.pull().call();
        }
        log("git over")
        git.close()
        overFlag = true
    }

    fun setGitConfig(repository: Repository) {
        repository.config
            .setBoolean("http", "", "sslVerify", false);
    }
}