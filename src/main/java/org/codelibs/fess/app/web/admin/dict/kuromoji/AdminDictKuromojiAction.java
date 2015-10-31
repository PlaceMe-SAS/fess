/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.admin.dict.kuromoji;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.app.service.KuromojiService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminDictKuromojiAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private KuromojiService kuromojiService;
    @Resource
    private KuromojiPager kuromojiPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected DynamicProperties crawlerProperties;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("kuromoji"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        return asHtml(path_AdminDictKuromoji_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        kuromojiPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminDictKuromoji_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        copyBeanToBean(form, kuromojiPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictKuromoji_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        kuromojiPager.clear();
        return asHtml(path_AdminDictKuromoji_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        return asHtml(path_AdminDictKuromoji_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        data.register("kuromojiItemItems", kuromojiService.getKuromojiList(form.dictId, kuromojiPager));

        // restore from pager
        BeanUtil.copyBeanToBean(kuromojiPager, form, op -> {
            op.exclude(Constants.PAGER_CONVERSION_RULE);
        });
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final String dictId) {
        return asHtml(path_AdminDictKuromoji_EditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.EDIT);
        return asHtml(path_AdminDictKuromoji_EditJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                kuromojiService.getKuromojiItem(dictId, id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.crudMode = crudMode;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDictKuromoji_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.EDIT;
        kuromojiService.getKuromojiItem(form.dictId, form.id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return asHtml(path_AdminDictKuromoji_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DELETE);
        return asHtml(path_AdminDictKuromoji_ConfirmJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                kuromojiService.getKuromojiItem(dictId, id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.crudMode = crudMode;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.DELETE;
        kuromojiService.getKuromojiItem(form.dictId, form.id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return asHtml(path_AdminDictKuromoji_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.CONFIRM);
        return asHtml(path_AdminDictKuromoji_ConfirmJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                kuromojiService.getKuromojiItem(dictId, id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final CreateForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminDictKuromoji_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.EDIT;
        return asHtml(path_AdminDictKuromoji_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse downloadpage(final String dictId) {
        return asHtml(path_AdminDictKuromoji_DownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            kuromojiService.getKuromojiFile(dictId).ifPresent(file -> {
                data.register("path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), toIndexHtml());
            });
        });
    }

    @Execute(token = TxToken.VALIDATE)
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        return kuromojiService.getKuromojiFile(form.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentType("text/plain; charset=UTF-8").stream(out -> {
                out.write(file.getInputStream());
            });
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), () -> downloadpage(form.dictId));
            return null;
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse uploadpage(final String dictId) {
        return asHtml(path_AdminDictKuromoji_UploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            kuromojiService.getKuromojiFile(dictId).ifPresent(file -> {
                data.register("path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), toIndexHtml());
            });
        });
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        return kuromojiService.getKuromojiFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.kuromojiFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                throwValidationError(messages -> messages.addErrorsFailedToUploadKuromojiFile(GLOBAL), () -> {
                    return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
                });
            }
            saveInfo(messages -> messages.addSuccessUploadKuromojiFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadKuromojiFile(GLOBAL), () -> uploadpage(form.dictId));
            return null;
        });

    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createKuromojiItem(form).ifPresent(entity -> {
            entity.setNewToken(form.token);
            entity.setNewSegmentation(form.segmentation);
            entity.setNewReading(form.reading);
            entity.setNewPos(form.pos);
            kuromojiService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        createKuromojiItem(form).ifPresent(entity -> {
            entity.setNewToken(form.token);
            entity.setNewSegmentation(form.segmentation);
            entity.setNewReading(form.reading);
            entity.setNewPos(form.pos);
            kuromojiService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DELETE);
        validate(form, messages -> {}, toEditHtml());
        kuromojiService.getKuromojiItem(form.dictId, form.id).ifPresent(entity -> {
            kuromojiService.delete(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    protected OptionalEntity<KuromojiItem> createKuromojiItem(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                final KuromojiItem entity = new KuromojiItem(0, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY);
                return OptionalEntity.of(entity);
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return kuromojiService.getKuromojiItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toIndexHtml() {
        return () -> {
            return redirect(AdminDictAction.class);
        };
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminDictKuromoji_EditJsp);
        };
    }
}