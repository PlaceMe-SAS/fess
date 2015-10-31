<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.suggest_bad_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="suggestBadWord" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.suggest_bad_word_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/suggestbadword">
							<la:message key="labels.suggest_bad_word_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.suggest_bad_word_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.suggest_bad_word_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.suggest_bad_word_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.suggest_bad_word_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.suggest_bad_word_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.suggest_bad_word_link_update" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/suggestbadword"
											styleClass="btn btn-default btn-xs">
											<la:message key="labels.suggest_bad_word_link_list" />
										</la:link>
										<la:link href="createpage" styleClass="btn btn-success btn-xs">
											<la:message key="labels.suggest_bad_word_link_create_new" />
										</la:link>
										<la:link href="downloadpage"
											styleClass="btn btn-primary btn-xs">
											<la:message key="labels.suggest_bad_word_link_download" />
										</la:link>
										<la:link href="uploadpage" styleClass="btn btn-success btn-xs">
											<la:message key="labels.suggest_bad_word_link_upload" />
										</la:link>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<div class="form-group">
										<label for="word" class="col-sm-3 control-label"><la:message
												key="labels.suggest_bad_word_suggest_word" /></label>
										<div class="col-sm-9">
											<la:text property="suggestWord" styleClass="form-control" />
										</div>
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.suggest_bad_word_button_back" />">
											<la:message key="labels.suggest_bad_word_button_back" />
										</button>
										<button type="submit" class="btn btn-success"
											name="confirmfromcreate"
											value="<la:message key="labels.suggest_bad_word_button_confirm" />">
											<la:message key="labels.suggest_bad_word_button_confirm" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.suggest_bad_word_button_back" />">
											<la:message key="labels.suggest_bad_word_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="confirmfromupdate"
											value="<la:message key="labels.suggest_bad_word_button_confirm" />">
											<la:message key="labels.suggest_bad_word_button_confirm" />
										</button>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
