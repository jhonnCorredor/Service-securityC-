using Entity.Model.Parameter;
using Entity.Model.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Operational
{
    public class Farm
    {
        public int Id { get; set; } 
        public string Name { get; set; }
        public int? DepartamentId { get; set; }
        public Departament? Departament { get; set; }
        public int? UserId { get; set; }
        public User? User { get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
