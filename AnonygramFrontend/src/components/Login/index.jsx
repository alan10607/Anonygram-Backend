import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from 'react-i18next';
import { isJwtValid } from '../../service/jwt';
import { locationTo } from '../../util/locationTo';
import { ICON_LOGO, VERSION, BACKEND_API_URL } from '../../util/constant';
import authService from '../../service/request/authService';
import './index.scss'
import { useLocalSetting } from '../../util/localSetting';

export default function Login() {
  const emailRef = useRef();
  const pwRef = useRef();
  const [email, setEmail] = useState();
  const [password, setPassword] = useState();
  const [hint, setHint] = useState("");
  const [logged, setlogged] = useState(false);
  const [{ jwt }, { setJwtByToken }] = useLocalSetting();
  const navigate = useNavigate();
  const { t } = useTranslation();

  useEffect(() => {//For testing, check user SSL confirmation
    authService.testSsl().then((res) => { })
      .catch((e) => {//If does not conform SSL then redirect to the backend
        const sslUrl = `${BACKEND_API_URL}/ssl?callbackUrl=${window.location.href}`;
        console.log("Redirect backend for ssl", sslUrl)
        locationTo(sslUrl);
      });
  }, []);

  useEffect(() => {//取得新的jwt後跳轉
    if (logged) {
      navigate("/hub");
    }
  }, [logged]);

  const login = (event) => {
    event.preventDefault();

    authService.login({ email, password }).then((res) => {
        setJwtByToken(res.token);
        setlogged(true);
        navigate("/hub");
      }).catch((e) => {
        setHint(t("login-err"));
      });
  }

  const loginAnony = (event) => {
    event.preventDefault();

    if (jwt.isVaild) {
      setDone(true);
    } else {
      authService.anony().then((res) => {
        setJwtByToken(res.token);
        setlogged(true);
        navigate("/hub");
      }).catch((e) => {
        setHint(t("login-anony-err"));
      });
    }
  }

  return (
    <div className="login center">
      <div>
        <img className="icon logo" src={ICON_LOGO} alt="ICON_LOGO" />
        <div className="col-flex">
          <form onSubmit={login}>
            <input value={email}
              onChange={(event) => { setEmail(event.target.value) }}
              type="text"
              placeholder="Email"
              autoComplete="on"
              required
              autoFocus />
            <input value={password}
              onChange={(event) => { setPassword(event.target.value) }}
              type="password"
              placeholder={t("pw")}
              autoComplete="on"
              required />
            {/* <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> */}
            <input type="submit" value={t("login")} />
          </form>
          <div className="login-info">
            <span>{t("no-account?")} </span>
            <Link to="/register" className="info-link">{t("register")}</Link>
          </div>
          <div className="hint">{hint}</div>
          <div className="line-word">{t("or")}</div>
          <input type="button" value={t("as-anony")} onClick={loginAnony} />
        </div>
        <div className="version">{VERSION}</div>
      </div>
    </div>
  )
}