<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Forget password</title>
  </head>
  <body>
 <div
      style="
        border: 3px solid rgba(0, 0, 0, 0.1);
        background: rgba(0, 0, 0, 0.05);
        margin: 0% 3% 0 3%;
        border-radius: 21px;
        
      "
    >
    <header>
        <div>
          <h1
            style="
              font-size: 22px;
              padding-left: 9%;
              padding-top: 2%;
              font-weight: bold !important;
              font-family: 'Times New Roman', Times, serif;
              -webkit-text-fill-color: rgb(180, 169, 169);
              -webkit-text-stroke-width: 1px;
              -webkit-text-stroke-color: black;
              padding-left: 40%;
            "
          >
            Intelizign PL
          </h1>
        </div>
    </header>
    <div
        style="
          display: flex;
          flex-direction: row;
          justify-content: center;
          padding-left: 4%;
        "
      >
        <div>
          <p
            style="
              font-size: 19px;
              font-family: 'Times New Roman', Times, serif;
            "
          >
            Hi ${recieverName},
          </p>
          <p style="font-size: 15px" class="content2">
            There was a request to reset your password!
          </p>
          <p style="font-size: 15px">
            If you did not make this request then please ignore this email.
          </p>
          <p style="font-size: 15px">
            Otherwise, please click this link to reset your password.
          </p>
          <p style="font-size: 15px">
            
            <a style="font-style: italic" href="${passwordlink}">
              Click Here</a>
          </p>
          <p
            style="
              font-size: 13px;
              font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman',
                serif;  margin-top: 20%;
            "
          >
            Thank you,
          </p>
          <p
            style="
              font-size: 12px;
              font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman',
                serif;
              margin-top: -1%;
              padding-left: 1%;
            "
          >
            Intelizign Team
          </p>
        </div>
    </div>
    <div style="display: flex; flex-direction: row; justify-content: center; padding-left: 40%;">
      <div>
        <h1 style="font-size: 10px;">Intelizign PL</h1>
        <p style="font-size: 10px;">© 2023 <a href="https://github.com/Binoagustus" id="about">Intelizign</a></p>
        
        
      </div>
    </div>
</div>
  </body>
</html>
