using Entity.Model.Parameter;

namespace Entity.Model.Security
{
    public class Person
    {
        public int Id { get; set; }
        public string First_name { get; set; }
        public string Last_name { get; set; }
        public string Email { get; set; }
        public string Addres { get; set; }
        public int Phone { get; set; }
        public string Type_document { get; set; }
        public string Document { get; set; }
        public int CityId { get; set; }
        public City City { get; set; }
        public Boolean State { get; set; }
        public DateTime Birth_of_date { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set;}
        public DateTime? Deleted_at { get; set; }
    }
}
