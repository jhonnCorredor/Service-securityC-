using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Entity.Model.Operational;

namespace Entity.Model.Parameter
{
    public class FumigationSupplies
    {
        public int Id { get; set; }
        public string Dose { get; set; }
        public int SuppliesId { get; set; }
        public Supplies Supplies { get; set; }
        public int FumigationId { get; set; }
        public Fumigation Fumigation { get; set; }
        public bool State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
