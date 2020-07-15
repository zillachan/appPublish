package com.step.ap.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.step.ap.base.BaseService;
import com.step.ap.entity.App;
import com.step.ap.entity.AppVersion;
import com.step.ap.exception.MyException;
import com.step.ap.vo.AppUploadVo;
import com.step.ap.vo.AppVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 服务类
 */
@Service
@Transactional
@AllArgsConstructor
public class AppService extends BaseService<App> {

    private final AppVersionService appVersionService;
    private final FileSystemStorageService storageService;

    public AppVo selectById(int id) {
        App app = super.getById(id);
        List<AppVersion> appVersions = appVersionService.selectByApp(id);
        AppVo appVo = app.toBean(AppVo.class);
        appVo.setVersions(appVersions);
        return appVo;
    }

    public App selectByPackageName(String packageName) {
        return super.getOne(new LambdaQueryWrapper<App>().eq(App::getPackageName, packageName));
    }

    public void uploadApk(AppUploadVo appUploadVo, MultipartFile file) {
        if (!ObjectUtils.allNotNull(appUploadVo.getPackageName(), appUploadVo.getName(), appUploadVo.getVersionName(), appUploadVo.getVersionCode())) {
            throw new MyException("应用名、包名、版本不能为空");
        }
        //若没有app则新增
        App app = selectByPackageName(appUploadVo.getPackageName());
        if (app == null) {
            app = new App();
            app.setName(appUploadVo.getName());
            app.setPackageName(appUploadVo.getPackageName());
            app.setShortCode(RandomStringUtils.randomAlphabetic(4).toLowerCase());
            super.save(app);
        }
        //新增版本
        AppVersion appVersion = appUploadVo.toBean(AppVersion.class);
        appVersion.setAppId(app.getId());
        appVersion.setSize(file.getSize() / 1024);
        appVersionService.save(appVersion);
        //更新app中currentId
        app.setCurrentVersionId(appVersion.getId());
        app.setCurrentVersionCode(appVersion.getVersionCode());
        app.setCurrentVersionName(appVersion.getVersionName());
        super.updateById(app);
        storageService.store(file);
    }
}
