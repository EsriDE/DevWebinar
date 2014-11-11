using System.Runtime.Serialization;

namespace OAuth2
{
    [DataContract(Name = "OAuth2TokenResult")]
    public class OAuth2TokenResult
    {
        [DataMember(Name = "access_token")]
        public string AccessToken;
        [DataMember(Name = "expires_in")]
        public string ExpiresIn;
        [DataMember(Name = "refresh_token")]
        public string RefreshToken;
        [DataMember(Name = "username")]
        public string Username;
        [DataMember(Name = "error")]
        public OAuth2TokenError Error;
    }
}
