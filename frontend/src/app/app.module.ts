import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {MatchComponent} from "./components/match/match.component";
import {UserProfileComponent} from './components/user-profile/user-profile.component';
import {NgOptimizedImage} from "@angular/common";
import {StudentsComponent} from './components/admin/students/students.component';
import {DashboardComponent} from './components/admin/dashboard/dashboard.component';
import {AdminComponent} from './components/admin/admin.component';
import {SubjectComponent} from "./components/admin/subjects/subjects.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastrModule} from "ngx-toastr";
import {RegisterComponent} from "./components/register/register.component";
import {NgxSpinnerModule} from "ngx-spinner";
import {ChatComponent} from "./components/chat/chat.component";
import {WebSocketService} from "./services/web-socket.service";
import {StarRatingComponent} from "./components/rating/rating.component";
import { ImportStatusComponent } from './components/admin/import-status/import-status.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    RegisterComponent,
    LoginComponent,
    MessageComponent,
    MatchComponent,
    AdminComponent,
    DashboardComponent,
    StudentsComponent,
    UserProfileComponent,
    SubjectComponent,
    ChatComponent,
    StarRatingComponent,
    ImportStatusComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    NgOptimizedImage,
    BrowserAnimationsModule,
    NgxSpinnerModule,
    ToastrModule.forRoot()
  ],
  providers: [httpInterceptorProviders, WebSocketService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
