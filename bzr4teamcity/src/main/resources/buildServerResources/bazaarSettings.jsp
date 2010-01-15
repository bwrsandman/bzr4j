<%@include file="/include.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<script type="text/javascript">
function updateBranchName(repoPath) {
  if (repoPath.indexOf('#') != -1 && $('branchName').value == '') {
    $('branchName').value = repoPath.substring(repoPath.indexOf('#')+1);
  }
}
</script>
<table class="runnerFormTable">

  <l:settingsGroup title="General Settings">
  <tr>
    <th><label for="bzrCommandPath">Bzr command path: <l:star/></label></th>
    <td><props:textProperty name="bzrCommandPath" className="longField" />
      <span class="error" id="error_bzrCommandPath"></span></td>
  </tr>
  <tr>
    <th><label for="repositoryPath">Pull changes from: <l:star/></label></th>
    <td><props:textProperty name="repositoryPath" className="longField" onchange="updateBranchName(this.value)"/>
      <span class="error" id="error_repositoryPath"></span></td>
  </tr>
  <tr>
    <th><label for="branchName">Branch name: </label></th>
    <td><props:textProperty name="branchName" /></td>
  </tr>
  </l:settingsGroup>
  <l:settingsGroup title="Authorization settings">
  <tr>
    <td colspan="2">You may require to provide authorization settings if you need to tag / label sources in the remote repository.</td>
  </tr>
  <tr>
    <th><label for="username">User name:</label></th>
    <td><props:textProperty name="username"/></td>
  </tr>
  <tr>
    <th><label for="secure:password">Password:</label></th>
    <td><props:passwordProperty name="secure:password"/></td>
  </tr>
  </l:settingsGroup>

</table>
