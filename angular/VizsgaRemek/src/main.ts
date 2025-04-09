/// <reference types="@angular/localize" />

import { provideHttpClient } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { registerLocaleData } from '@angular/common';
import localeHu from '@angular/common/locales/hu';

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

  registerLocaleData(localeHu);
