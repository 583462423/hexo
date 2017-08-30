---
title: git笔记
date: 2017-07-31 08:54:49
tags: git
---

书籍:progit

<!--more-->

HEAD 是一个指针，指向当前的分支名，当前分支名也是一个指针，指向一个特定的库。

|命令|解释|
|-|-|
|git add .|添加文件到暂存区|
|git reset HEAD <file>|将文件从暂存区撤销|
|git status|显示当前状态|
|git diff|显示已暂存和未暂存的修改，即修改了哪些内容|
|git diff --staged|显示已暂存和已提交的修改，即将要提交的修改|
|git rm <file>|移除文件|
|git rm -f <file>|强制移除暂存区中文件|
|git rm --cached <file>|仅暂存区中移除，工作目录中保留|
|git log|查看提交历史|
|git log -p|查看提交历史并显示每次提交内容的差异|
|git log -2|显示最近两次提交|
| git log --oneline --decorate --graph --all|类似图形界面的方式显示提交历史，并能显示分叉|
|git commit --amend|忘记提交某些文件的时候使用，最终只有一次提交|
|git checkout -- <file>|将文件恢复为提交前的状态|
|git remote|列出每一个远程服务器的简写|
|git remote -v|列出远程仓库所对应的url|
|git remote add <shorname> <url>|添加新的远程仓库|
|git fetch [remote-name]|从远程仓库中拉去所有你还没有的数据，但不会合并数据|
|git pull|自动抓取跟踪的远程分支的数据并合并|
|git push [remote-name] [branch-name]|推送分支内容到远程仓库中，因origin是默认，master也是默认，所以有这样的命令git push origin master|
|git remote show [remote-name]|查看远程仓库|
|git remote rename [remote-name] [new name]|修改远程仓库简写|
|git remote rm [remote-name]|移除本地远程仓库|
|git config --global alias.co checkout|给checkout起个别名co，以后命令中可以使用co代替checkout|
|git branch [branch-name]|创建分支，实际上是创建一个指针，指向当前分支|
|git log --oneline --decorate|显示日志，并现实HEAD对应的分支|
|git chekcout [branch-name]|切换分支，实际上是修改HEAD指针指向对应的分支指针|
|git merge [branch-name]|将当前分支和branch-name对应的反之进行合并，合并后的分支依然为当前分支名|
|git branch -d [branch-name]|合并分支完成后，两个分支指向同一个库，可以使用该命令删掉一个分支|
|git branch|显示所有分支和当前分支|
|git branch -v|现实所有分支和每次的提交|
|git push origin HEAD:[remote-branch-name]|推送本地当前分支到远程分支中去|
|git checkout --trace origin/serverfix|将当前分支跟踪远程的serverfix分支|
|git checkout -b [branch-name] [remotename]/[branch]| 创建分支并跟踪远程某一分支|
|git branch -vv|查看所有的本地分支并包含每个分支跟踪哪个远程分支|
|git rebase [branchname]|变基，将当前分支应用到branchname分支上，操作过程是，将当前分支的修改文件，重新应用到branchname上，然后生成一个新的分支，名字依然是当前分支名，与merge有很大的区别|
|git stash|存储当前尚未提交数据到栈中，取则为git stash pop|
|git reset 9e5e64a|reset和checkout很大的区别是，checkout切换分支的时候，是将HEAD指向一个分支，而reset则是将HEAD指向的分支去指向另一个分支|
|git reset --soft HEAD~|reset到head的父节点|
|git reset --hard HEAD~|reset到head的父节点，并删除掉之前提交和add的内容，比较危险的操作|
|git cherry-pick <commit_id>|将已提交的其他分支的记录，应用到当前分支|

在合并的时候，有两种合并方式，一种是fast-forward，这种合并是直接将master指向其子节点分支。还有一种就是分叉时候的合并，将两个节点的共同父节点取出，搞个三方合并，生成一个新的快照，且该快照又是两个分叉节点的子节点，这样就完事了。

