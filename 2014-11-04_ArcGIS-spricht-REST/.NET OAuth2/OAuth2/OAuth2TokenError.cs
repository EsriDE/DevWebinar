using System.Runtime.Serialization;

namespace OAuth2
{
    [DataContract(Name = "OAuth2TokenError")]
    public class OAuth2TokenError
    {
        [DataMember(Name = "message")]
        public string Message;
        [DataMember(Name = "details")]
        public string Details;
        [DataMember(Name = "error_description")]
        public string ErrorDescription;
    }
}
