import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent, LoginMode} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from "./components/register/register.component";
import {VerifyComponent} from "./components/register/verify/verify.component";
import {MatchComponent} from "./components/match/match.component";
import {AdminComponent} from './components/admin/admin.component';
import {DashboardComponent} from './components/admin/dashboard/dashboard.component';
import {StudentsComponent} from './components/admin/students/students.component';
import {UserMode, UserProfileComponent} from './components/user-profile/user-profile.component';
import {SubjectComponent} from "./components/admin/subjects/subjects.component";
import {PasswordResetComponent} from "./components/password-reset/password-reset.component";
import {RequestResetComponent} from "./components/password-reset/request-reset/request-reset.component";
import {FeedbackComponent} from "./components/feedback/feedback.component";
import {AdminFeedbackComponent} from "./components/admin/feedback/admin-feedback.component";
import {ChatComponent} from "./components/chat/chat.component";
import {ReportsComponent} from "./components/admin/reports/reports.component";

const routes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent, data: {mode: LoginMode.user} },
  {
    path: 'register', children: [
      {path: '', component: RegisterComponent},
      {path: 'verify/:token', component: VerifyComponent}
    ]
  },
  {    path: 'password_reset', children: [
      {path: '', component: RequestResetComponent},
      {path: ':token', component: PasswordResetComponent}
    ]
  },
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'matches', component: MatchComponent},
  {path: 'myprofile', component: UserProfileComponent, data: {mode: UserMode.user}},
  {path: 'feedback', component: FeedbackComponent, data: {mode: UserMode.user}},
  {path: 'chat', component: ChatComponent},
  {path: 'myprofile', component: UserProfileComponent,data: {mode: UserMode.user}},
  {
    path: 'admin', component: AdminComponent, children: [
      {path: 'login', component: LoginComponent, data: {mode: LoginMode.admin}},
      {path: 'dashboard', component: DashboardComponent},
      {path: 'students', component: StudentsComponent},
      {path: 'students/:id', component: UserProfileComponent, data: {mode: UserMode.admin}},
      {path: 'students/feedback/:id', component: AdminFeedbackComponent},
      {path: 'subjects', component: SubjectComponent},
      {path: 'report', component: ReportsComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
