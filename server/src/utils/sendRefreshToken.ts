import { Response } from "express";

export const sendRefreshToken = (res: Response, token: string) => {
  res.cookie('jid', token,
    {
      httpOnly: true,
      path: "/refresh_token",
      // allows cross site cookies to be sent (different domain names)
      // must allow third party cookies in browser
      // I believe not necessary if you are going to use one domain name
      sameSite: "none",
      secure: true
    }
  );
}